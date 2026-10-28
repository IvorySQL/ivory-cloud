import com.highgo.cloud.util.CommonUtil;

/** Complete real class, genuine JDK Class values, no copied classifier or framework doubles. */
public final class CommonUtilClassPredicateRegression {
    private static int failures;
    private static int cases;

    private static void expect(Class<?> type, boolean expected) {
        cases++;
        try {
            boolean actual = CommonUtil.isNumber(type);
            if (actual != expected) {
                throw new AssertionError(type + ": expected " + expected + ", got " + actual);
            }
            System.out.println("PASS " + type.getTypeName());
        } catch (Throwable error) {
            failures++;
            System.err.println("FAIL " + type.getTypeName() + ": " + error);
            error.printStackTrace(System.err);
        }
    }

    public static void main(String[] args) {
        expect(byte.class, true);
        expect(short.class, true);
        expect(int.class, true);
        expect(long.class, true);
        expect(float.class, true);
        expect(double.class, true);
        expect(Object.class, false);
        expect(CharSequence.class, false);
        expect(Integer.class, true);
        expect(String.class, false);
        if (failures != 0) {
            throw new AssertionError(failures + " of " + cases + " cases failed");
        }
        System.out.println(cases + " cases passed");
    }
}
