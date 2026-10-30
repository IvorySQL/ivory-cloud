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

import com.highgo.cloud.util.ShellUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/** Full production ShellUtil tests with actual local processes, no process mock. */
public final class ShellTimeoutRegression {
    private static int passed;
    private static int failed;

    private interface Check {
        void run() throws Exception;
    }

    private static void check(String name, Check check) {
        try {
            check.run();
            passed++;
            System.out.println("PASS " + name);
        } catch (Throwable error) {
            failed++;
            System.err.println("FAIL " + name);
            error.printStackTrace(System.err);
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static String exitCommand(int status) {
        // Runtime.exec(String) tokenizes on whitespace. These fixed shell
        // commands intentionally need no quoting or user-supplied interpolation.
        return "cmd.exe /d /c exit " + status;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Supply Main's existing private work directory");
        }
        Path root = Paths.get(args[0]).toRealPath();
        require(Files.isDirectory(root), "Private work directory must exist");
        require(root.getParent() != null, "Private work directory must not be a filesystem root");
        Path missing = root.resolve("nonexistent-ivory-command-" + UUID.randomUUID() + ".exe");
        require(Files.notExists(missing), "Missing-command fixture must not exist");

        check("launch IOException returns false", () -> {
            boolean completed = ShellUtil.execShellWithTimeout(
                    missing.toString(), 5, TimeUnit.SECONDS);
            require(!completed, "A process that could not start cannot have completed");
        });
        check("successful local process still completes", () -> {
            String command = System.getProperty("os.name").startsWith("Windows")
                    ? exitCommand(0) : "/bin/true";
            require(ShellUtil.execShellWithTimeout(command, 5, TimeUnit.SECONDS),
                    "An actual process exiting promptly must complete");
        });
        check("nonzero termination retains completion semantics", () -> {
            String command = System.getProperty("os.name").startsWith("Windows")
                    ? exitCommand(7) : "/bin/false";
            require(ShellUtil.execShellWithTimeout(command, 5, TimeUnit.SECONDS),
                    "Existing waitFor semantics report termination, including a nonzero exit");
        });
        System.out.println("RESULT passed=" + passed + " failed=" + failed);
        if (failed != 0) {
            throw new AssertionError("Regression failures: " + failed);
        }
    }
}
