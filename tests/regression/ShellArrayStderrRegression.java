import com.highgo.cloud.util.ShellUtil;
import java.io.File;
import java.util.Arrays;

/** Exercises the complete executor with real, self-bounded JDK child processes. */
public final class ShellArrayStderrRegression {
    private static int failures;

    private static void check(String name, String mode, int exitCode, String argument) {
        String executable = new File(
                new File(System.getProperty("java.home"), "bin"),
                System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java")
                .getAbsolutePath();
        String[] command = {
            executable, "-Xmx32m", "-XX:ActiveProcessorCount=1",
            "-cp", System.getProperty("java.class.path"),
            Emitter.class.getName(), mode, Integer.toString(exitCode), argument
        };
        int actual = ShellUtil.execShellSpaceParam(command);
        if (actual != exitCode) {
            failures++;
            System.err.println("FAIL " + name + ": expected exit " + exitCode + ", got " + actual);
        } else {
            System.out.println("PASS " + name);
        }
    }

    public static void main(String[] args) {
        check("large-stderr-zero-exit", "stderr", 0, "ordinary");
        check("large-stderr-nonzero-exit", "stderr", 7, "ordinary");
        check("stdout-only-control", "stdout", 0, "ordinary");
        check("quiet-nonzero-control", "quiet", 7, "ordinary");
        check("array-argument-with-spaces-control", "argument", 0, "literal argument with spaces");
        if (failures != 0) {
            throw new AssertionError(failures + " process completion assertions failed");
        }
    }

    public static final class Emitter {
        public static void main(String[] args) throws Exception {
            // Bound THIS private child, including a blocked pipe write, without
            // killing unrelated processes or depending on the production helper's timeout policy.
            Thread watchdog = new Thread(() -> {
                try {
                    Thread.sleep(8000);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    return;
                }
                Runtime.getRuntime().halt(99);
            }, "private-emitter-watchdog");
            watchdog.setDaemon(true);
            watchdog.start();

            String mode = args[0];
            int exitCode = Integer.parseInt(args[1]);
            if ("stderr".equals(mode)) {
                byte[] chunk = new byte[8192];
                Arrays.fill(chunk, (byte) 'e');
                chunk[chunk.length - 1] = (byte) '\n';
                for (int i = 0; i < 64; i++) {
                    System.err.write(chunk);
                    System.err.flush();
                }
                System.out.println("stderr fully written");
            } else if ("stdout".equals(mode)) {
                System.out.println("ordinary stdout");
            } else if ("argument".equals(mode)) {
                if (args.length != 3 || !"literal argument with spaces".equals(args[2])) {
                    Runtime.getRuntime().halt(83);
                }
                System.out.println(args[2]);
            } else if (!"quiet".equals(mode)) {
                Runtime.getRuntime().halt(84);
            }
            System.exit(exitCode);
        }
    }
}

