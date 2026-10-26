/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/** Real class-loader and filesystem regression for the complete production SshUtil. */
public final class ResourcePathRegression {
    private static int passed;
    private static int failed;

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void runCase(String name, String suffix, boolean resourceWalk, String[] args) {
        try {
            Path privateRoot = Paths.get(args[0]).toRealPath();
            Path classes = Paths.get(args[1]).toRealPath();
            Path realSpring = Paths.get(args[2]).toRealPath();
            require(Files.isDirectory(privateRoot) && privateRoot.getParent() != null,
                    "Main must supply a private non-root directory");
            Path work = Files.createTempDirectory(privateRoot, "ivory-resource-path-").toRealPath();
            require(work.getParent().equals(privateRoot), "Unexpected private fixture boundary");
            Path springHome = Files.createDirectory(work.resolve(suffix));
            Path springFixture = springHome.resolve("spring-core.jar");
            // Copy the existing real dependency byte-for-byte only into this
            // runtime fixture; no dependency source or archive is modified.
            Files.copy(realSpring, springFixture);
            require(Files.size(realSpring) == Files.size(springFixture), "Incomplete dependency copy");
            Path resourceHome = Files.createDirectory(work.resolve("resources-" + suffix));
            Files.createDirectories(resourceHome.resolve("monitor/ivory"));
            Files.copy(classes.resolve("ResourcePathMarker.class"),
                    resourceHome.resolve("ResourcePathMarker.class"));

            List<URL> urls = new ArrayList<>();
            urls.add(resourceHome.toUri().toURL());
            urls.add(classes.toUri().toURL());
            urls.add(springFixture.toUri().toURL());
            for (int i = 3; i < args.length; i++) {
                urls.add(Paths.get(args[i]).toRealPath().toUri().toURL());
            }
            // Do not delegate to the system application's Spring copy: the
            // production code must see the real encoded fixture CodeSource.
            ClassLoader platform = ClassLoader.getSystemClassLoader().getParent();
            try (URLClassLoader loader = new URLClassLoader(urls.toArray(new URL[0]), platform)) {
                Class<?> ssh = Class.forName("com.highgo.cloud.util.SshUtil", true, loader);
                if (resourceWalk) {
                    Class<?> marker = Class.forName("ResourcePathMarker", true, loader);
                    ssh.getMethod("copyDirFilesFromJar", Class.class, String.class)
                            .invoke(null, marker, "monitor/ivory");
                    // Empty but valid classpath directories exercise the real
                    // IDE traversal without writing into the dependency cache.
                    try (Stream<Path> entries = Files.list(springHome)) {
                        require(entries.count() == 1, "Empty traversal unexpectedly copied files");
                    }
                } else {
                    String actual = (String) ssh.getMethod("getCurrentDirPath").invoke(null);
                    require(Paths.get(actual).toAbsolutePath().normalize().equals(springHome),
                            "CodeSource directory was not decoded exactly once: " + actual
                                    + " expected " + springHome);
                }
            }
            passed++;
            System.out.println("PASS " + name);
        } catch (Throwable error) {
            failed++;
            System.err.println("FAIL " + name);
            Throwable cause = error instanceof InvocationTargetException
                    ? ((InvocationTargetException) error).getCause() : error;
            cause.printStackTrace(System.err);
        }
        // Main owns cleanup of its private root after the JVM has released JARs.
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            throw new IllegalArgumentException(
                    "privateRoot classes realSpringCore.jar otherRealDependency.jar ...");
        }
        Path root = Paths.get(args[0]).toRealPath();
        require(root.toUri().getRawPath().equals(root.toUri().getPath()),
                "Use Main's private ASCII path without spaces/percent escapes so plain controls stay plain");
        runCase("CodeSource directory with spaces", "library space", false, args);
        runCase("CodeSource directory with Unicode", "库目录", false, args);
        runCase("plain CodeSource control", "plain", false, args);
        runCase("literal plus CodeSource control", "literal+plus", false, args);
        runCase("classpath directory with spaces", "resource space", true, args);
        runCase("plain classpath directory control", "resource-plain", true, args);
        System.out.println("RESULT passed=" + passed + " failed=" + failed);
        if (failed != 0) {
            throw new AssertionError("Regression failures: " + failed);
        }
    }
}
