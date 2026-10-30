import com.highgo.cloud.util.toolkit.StringUtil;

/** JDK assertion runner; the complete production class uses real Spring Core. */
public final class StringUtilReverseSearchRegression {
    private static int failures;

    private static void check(String name, int expected, int actual) {
        if (expected != actual) {
            failures++;
            System.err.println("FAIL " + name + ": expected " + expected + " but was " + actual);
        } else {
            System.out.println("PASS " + name);
        }
    }

    public static void main(String[] args) {
        check("two-argument-prefix", 0,
                StringUtil.lastIndexOfIgnoreCase("ABCxyz", "abc"));
        check("three-argument-zero-start", 0,
                StringUtil.lastIndexOfIgnoreCase("abcABC", "abc", 0));
        check("generic-identical-case-at-zero", 0,
                StringUtil.lastIndexOf("abc", "abc", 0, true));
        check("charsequence-prefix", 0,
                StringUtil.lastIndexOfIgnoreCase(
                        new StringBuilder("AbCd"), new StringBuilder("aB"), 4));

        check("case-sensitive-zero-control", 0,
                StringUtil.lastIndexOf("abc", "abc", 0, false));
        check("later-match-control", 3,
                StringUtil.lastIndexOfIgnoreCase("aBcAbC", "abc"));
        check("absent-control", -1,
                StringUtil.lastIndexOfIgnoreCase("abc", "z"));
        check("empty-needle-control", 0,
                StringUtil.lastIndexOfIgnoreCase("abc", "", 0));
        if (failures != 0) {
            throw new AssertionError(failures + " reverse-search cases failed");
        }
    }
}

