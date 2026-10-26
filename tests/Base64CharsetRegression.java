import com.highgo.cloud.util.toolkit.Base64Util;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** Calls the complete production codec; JDK Base64 is the independent oracle. */
public final class Base64CharsetRegression {
    private static int failures;

    private interface CheckedCase {
        void run();
    }

    private static void check(String name, CheckedCase test) {
        try {
            test.run();
            System.out.println("PASS " + name);
        } catch (RuntimeException | AssertionError failure) {
            failures++;
            System.err.println("FAIL " + name + ": " + failure);
        }
    }

    private static void equal(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("expected <" + expected + "> but was <" + actual + ">");
        }
    }

    private static String expected(String payload, Charset charset, boolean urlSafe) {
        Base64.Encoder encoder = urlSafe ? Base64.getUrlEncoder() : Base64.getEncoder();
        return encoder.encodeToString(payload.getBytes(charset));
    }

    private static String encode(String payload, Charset charset, boolean urlSafe) {
        return urlSafe ? Base64Util.encodeUrlSafe(payload, charset) : Base64Util.encode(payload, charset);
    }

    private static String decode(String encoded, Charset charset, boolean urlSafe) {
        return urlSafe ? Base64Util.decodeUrlSafe(encoded, charset) : Base64Util.decode(encoded, charset);
    }

    private static void runMode(boolean urlSafe) {
        String prefix = urlSafe ? "url-safe-" : "basic-";
        check(prefix + "UTF16-ASCII-carrier", () ->
                equal(expected("A", StandardCharsets.UTF_16, urlSafe),
                        encode("A", StandardCharsets.UTF_16, urlSafe)));
        check(prefix + "UTF16-canonical-decode", () ->
                equal("A", decode(expected("A", StandardCharsets.UTF_16, urlSafe),
                        StandardCharsets.UTF_16, urlSafe)));
        check(prefix + "UTF16-round-trip", () ->
                equal("A", decode(encode("A", StandardCharsets.UTF_16, urlSafe),
                        StandardCharsets.UTF_16, urlSafe)));
        check(prefix + "UTF16LE-ASCII-carrier", () ->
                equal(expected("A", StandardCharsets.UTF_16LE, urlSafe),
                        encode("A", StandardCharsets.UTF_16LE, urlSafe)));
        check(prefix + "default-UTF8-control", () -> {
            String payload = "\u4e2d\u6587";
            String encoded = urlSafe ? Base64Util.encodeUrlSafe(payload) : Base64Util.encode(payload);
            equal(expected(payload, StandardCharsets.UTF_8, urlSafe), encoded);
            equal(payload, urlSafe ? Base64Util.decodeUrlSafe(encoded) : Base64Util.decode(encoded));
        });
        check(prefix + "explicit-Latin1-control", () -> {
            String payload = "caf\u00e9";
            String encoded = encode(payload, StandardCharsets.ISO_8859_1, urlSafe);
            equal(expected(payload, StandardCharsets.ISO_8859_1, urlSafe), encoded);
            equal(payload, decode(encoded, StandardCharsets.ISO_8859_1, urlSafe));
        });
    }

    public static void main(String[] args) {
        runMode(false);
        runMode(true);
        if (failures != 0) {
            throw new AssertionError(failures + " codec cases failed");
        }
    }
}

