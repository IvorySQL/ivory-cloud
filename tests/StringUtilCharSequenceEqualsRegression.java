import com.highgo.cloud.util.toolkit.StringUtil;

/** Exercises the complete production class and its real Spring superclass. */
public final class StringUtilCharSequenceEqualsRegression {
    private static int failures;

    private static void check(String name, boolean expected, boolean actual) {
        if (expected != actual) {
            failures++;
            System.err.println("FAIL " + name + ": expected " + expected + " but was " + actual);
        } else {
            System.out.println("PASS " + name);
        }
    }

    public static void main(String[] args) {
        check("distinct-builders-two-argument", true,
                StringUtil.equals(new StringBuilder("same"), new StringBuilder("same")));
        check("distinct-buffers-explicit-false", true,
                StringUtil.equals(new StringBuffer("same"), new StringBuffer("same"), false));
        check("string-to-builder", true,
                StringUtil.equals("same", new StringBuilder("same")));
        check("builder-to-string", true,
                StringUtil.equals(new StringBuilder("same"), "same"));
        check("buffer-to-builder", true,
                StringUtil.equals(new StringBuffer("same"), new StringBuilder("same"), false));
        check("builder-to-buffer", true,
                StringUtil.equals(new StringBuilder("same"), new StringBuffer("same"), false));

        check("string-value-control", true,
                StringUtil.equals(new String("same"), new String("same")));
        check("unequal-content-control", false,
                StringUtil.equals(new StringBuilder("left"), new StringBuffer("right")));
        check("case-sensitive-control", false,
                StringUtil.equals(new StringBuilder("Same"), new StringBuffer("same"), false));
        check("ignore-case-control", true,
                StringUtil.equalsIgnoreCase(new StringBuilder("Same"), new StringBuffer("same")));
        StringBuilder shared = new StringBuilder("same");
        check("same-instance-control", true, StringUtil.equals(shared, shared));
        check("both-null-control", true, StringUtil.equals(null, null));
        check("left-null-control", false, StringUtil.equals(null, new StringBuilder("same")));
        check("right-null-control", false, StringUtil.equals(new StringBuffer("same"), null, false));
        if (failures != 0) {
            throw new AssertionError(failures + " CharSequence equality cases failed");
        }
    }
}
