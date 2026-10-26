import com.highgo.cloud.util.toolkit.ConcurrentDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/** Standalone JDK-only regression against the complete production class. */
public final class ConcurrentDateFormatTimezoneRegression {
    private static int failures;

    private interface CheckedCase {
        void run() throws Exception;
    }

    private static void check(String name, CheckedCase test) {
        try {
            test.run();
            System.out.println("PASS " + name);
        } catch (Exception | AssertionError failure) {
            failures++;
            System.err.println("FAIL " + name + ": " + failure);
        }
    }

    private static ConcurrentDateFormat formatter(String pattern, String zone) {
        return ConcurrentDateFormat.of(pattern, Locale.US, TimeZone.getTimeZone(zone));
    }

    private static void equal(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("expected <" + expected + "> but was <" + actual + ">");
        }
    }

    public static void main(String[] args) {
        check("named-z-parse-preserves-UTC-format", () -> {
            ConcurrentDateFormat f = formatter("yyyy-MM-dd HH:mm:ss z", "UTC");
            equal("1970-01-01 00:00:00 UTC", f.format(new Date(0)));
            Date parsed = f.parse("1970-01-01 00:00:00 PST");
            equal(Long.valueOf(28800000L), Long.valueOf(parsed.getTime()));
            equal("1970-01-01 00:00:00 UTC", f.format(new Date(0)));
            equal("1970-01-01 08:00:00 UTC", f.format(parsed));
        });

        check("named-zone-with-Z-pattern-preserves-UTC-format", () -> {
            // SimpleDateFormat's RFC822 pattern also accepts general zone names.
            ConcurrentDateFormat f = formatter("yyyy-MM-dd HH:mm:ss Z", "UTC");
            Date parsed = f.parse("1970-01-01 00:00:00 PST");
            equal(Long.valueOf(28800000L), Long.valueOf(parsed.getTime()));
            equal("1970-01-01 00:00:00 +0000", f.format(new Date(0)));
        });

        check("named-parse-preserves-configured-non-UTC-zone", () -> {
            ConcurrentDateFormat f = formatter("yyyy-MM-dd HH:mm:ss Z", "GMT+02:00");
            equal("1970-01-01 02:00:00 +0200", f.format(new Date(0)));
            Date parsed = f.parse("1970-01-01 00:00:00 PST");
            equal(Long.valueOf(28800000L), Long.valueOf(parsed.getTime()));
            equal("1970-01-01 02:00:00 +0200", f.format(new Date(0)));
        });

        check("numeric-offset-parse-control", () -> {
            ConcurrentDateFormat f = formatter("yyyy-MM-dd HH:mm:ss Z", "UTC");
            Date parsed = f.parse("1970-01-01 00:00:00 +0900");
            equal(Long.valueOf(-32400000L), Long.valueOf(parsed.getTime()));
            equal("1970-01-01 00:00:00 +0000", f.format(new Date(0)));
        });

        check("zone-free-parse-control", () -> {
            ConcurrentDateFormat f = formatter("yyyy-MM-dd HH:mm:ss", "UTC");
            equal(Long.valueOf(0), Long.valueOf(f.parse("1970-01-01 00:00:00").getTime()));
            equal("1970-01-01 00:00:00", f.format(new Date(0)));
        });

        check("parse-exception-and-following-format-control", () -> {
            ConcurrentDateFormat f = formatter("yyyy-MM-dd z HH:mm:ss", "UTC");
            try {
                f.parse("1970-01-01 PST not-a-time");
                throw new AssertionError("invalid input must throw ParseException");
            } catch (ParseException expected) {
                // Exception propagation and subsequent public operations stay intact.
            }
            equal("1970-01-01 UTC 00:00:00", f.format(new Date(0)));
            equal(Long.valueOf(0), Long.valueOf(f.parse("1970-01-01 UTC 00:00:00").getTime()));
        });

        if (failures != 0) {
            throw new AssertionError(failures + " regression cases failed");
        }
    }
}

