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

import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/** Full SshUtil, real Spring resource resolution, and nonempty private files. */
public final class ResourceFileRegression {
    private static final String XML_PATH = "monitor/ivory/alert-rule.xml";
    private static final String BINARY_PATH = "monitor/ivory/dashboards/chart.bin";
    private static final byte[] BINARY = {0, 1, -1, 10, 13, 42};
    private static int passed;
    private static int failed;

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void verifyBytes(Path target, byte[] expected) throws Exception {
        require(Files.isRegularFile(target), "Missing copied file: " + target);
        require(Arrays.equals(expected, Files.readAllBytes(target)),
                "Copied bytes differ: " + target);
    }

    private static void verifyXml(Path target) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(target.toFile());
        require("alert-rule".equals(doc.getDocumentElement().getNodeName()),
                "Wrong XML root");
        NodeList rules = doc.getElementsByTagName("rule");
        require(rules.getLength() == 4, "Expected four official rules");
        String[] names = {"CPULow", "CPUHigh", "MemoryLow", "MemoryHigh"};
        String[] thresholds = {"30", "80", "30", "80"};
        for (int i = 0; i < names.length; i++) {
            Element rule = (Element) rules.item(i);
            require(names[i].equals(rule.getAttribute("name")), "Wrong rule name");
            Element threshold = (Element) rule.getElementsByTagName("threshold").item(0);
            require(thresholds[i].equals(threshold.getAttribute("value")),
                    "Wrong threshold");
        }
    }

    private static URL resourceUrl(Path resources, Path work, boolean jar) throws Exception {
        if (!jar) {
            return resources.toUri().toURL();
        }
        Path archive = work.resolve("application-resources.jar");
        try (OutputStream output = Files.newOutputStream(archive);
                JarOutputStream zip = new JarOutputStream(output);
                Stream<Path> paths = Files.walk(resources)) {
            for (Path file : paths.collect(Collectors.toList())) {
                String relative = resources.relativize(file).toString().replace(File.separatorChar, '/');
                String entry = "BOOT-INF/classes/" + relative;
                if (Files.isDirectory(file) && !entry.endsWith("/")) {
                    entry += "/";
                }
                zip.putNextEntry(new JarEntry(entry));
                if (Files.isRegularFile(file)) {
                    Files.copy(file, zip);
                }
                zip.closeEntry();
            }
        }
        return new URL("jar:" + archive.toUri().toURL() + "!/BOOT-INF/classes/");
    }

    private static void runCase(String name, String requested, boolean directory,
            boolean jar, String[] args) {
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        try {
            Path privateRoot = Paths.get(args[1]).toRealPath();
            Path classes = Paths.get(args[2]).toRealPath();
            Path realSpring = Paths.get(args[3]).toRealPath();
            byte[] officialXml = Files.readAllBytes(Paths.get(args[4]).toRealPath());
            require(Files.isDirectory(privateRoot) && privateRoot.getParent() != null,
                    "Main must supply a private non-root directory");
            Path work = Files.createTempDirectory(privateRoot, "ivory-resource-file-").toRealPath();
            require(work.getParent().equals(privateRoot), "Invalid private fixture boundary");
            Path library = Files.createDirectory(work.resolve("library"));
            Path resources = Files.createDirectory(work.resolve("resources"));
            Path springCopy = library.resolve("spring-core.jar");
            Files.copy(realSpring, springCopy);
            verifyBytes(springCopy, Files.readAllBytes(realSpring));
            Files.copy(classes.resolve("ResourcePathMarker.class"),
                    resources.resolve("ResourcePathMarker.class"));
            Path sourceXml = resources.resolve(XML_PATH);
            Path sourceBinary = resources.resolve(BINARY_PATH);
            Files.createDirectories(sourceBinary.getParent());
            Files.write(sourceXml, officialXml);
            Files.write(sourceBinary, BINARY);
            require(!library.startsWith(resources) && !resources.startsWith(library),
                    "Source and destination must be separate private trees");

            List<URL> urls = new ArrayList<>();
            urls.add(resourceUrl(resources, work, jar));
            urls.add(classes.toUri().toURL());
            urls.add(springCopy.toUri().toURL());
            for (int i = 5; i < args.length; i++) {
                urls.add(Paths.get(args[i]).toRealPath().toUri().toURL());
            }
            ClassLoader platform = ClassLoader.getSystemClassLoader().getParent();
            try (URLClassLoader loader = new URLClassLoader(urls.toArray(new URL[0]), platform)) {
                Thread.currentThread().setContextClassLoader(loader);
                Class<?> ssh = Class.forName("com.highgo.cloud.util.SshUtil", true, loader);
                Class<?> marker = Class.forName("ResourcePathMarker", true, loader);
                require(marker.getClassLoader() == loader, "Fixture marker delegated unexpectedly");
                String destination = (String) ssh.getMethod("getCurrentDirPath").invoke(null);
                require(Paths.get(destination).toRealPath().equals(library),
                        "Copy must target the private Spring CodeSource parent");
                ssh.getMethod("copyDirFilesFromJar", Class.class, String.class)
                        .invoke(null, marker, requested);
                if (directory || XML_PATH.equals(requested)) {
                    verifyBytes(library.resolve(XML_PATH), officialXml);
                    verifyXml(library.resolve(XML_PATH));
                }
                if (directory || BINARY_PATH.equals(requested)) {
                    verifyBytes(library.resolve(BINARY_PATH), BINARY);
                }
                verifyBytes(sourceXml, officialXml);
                verifyBytes(sourceBinary, BINARY);
                Thread.currentThread().setContextClassLoader(previous);
            }
            passed++;
            System.out.println("PASS " + name);
        } catch (Throwable error) {
            failed++;
            System.err.println("FAIL " + name);
            Throwable cause = error instanceof InvocationTargetException
                    ? ((InvocationTargetException) error).getCause() : error;
            cause.printStackTrace(System.err);
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
        // Main owns private-root cleanup after all real JAR handles close.
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 6 || !("ide".equals(args[0]) || "jar".equals(args[0]))) {
            throw new IllegalArgumentException(
                    "ide|jar privateRoot classes realSpring.jar officialAlertXml otherDependency.jar ...");
        }
        boolean jar = "jar".equals(args[0]);
        if (jar && File.separatorChar != '/') {
            throw new UnsupportedOperationException(
                    "Existing JAR branch compares native Paths separators with BOOT-INF/classes/; "
                    + "run this control on Unix, do not count it as a Windows pass or skip");
        }
        runCase("official single XML resource", XML_PATH, false, jar, args);
        runCase("single binary resource", BINARY_PATH, false, jar, args);
        runCase("nonempty recursive directory control", "monitor/ivory", true, jar, args);
        System.out.println("RESULT passed=" + passed + " failed=" + failed);
        if (failed != 0) {
            throw new AssertionError("Regression failures: " + failed);
        }
    }
}
