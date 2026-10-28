import com.highgo.cloud.util.BeanUtil;
import java.util.HashMap;
import java.util.Map;

/** Full real BeanUtil entry points; fixtures are ordinary JavaBeans, not framework doubles. */
public final class BeanReadablePropertiesRegression {
    private static int failures;

    public static final class WriteOnlyBean {
        private String name;
        private String note;
        private String secret;

        public String getName() { return name; }
        public void setName(String value) { name = value; }
        public String getNote() { return note; }
        public void setNote(String value) { note = value; }
        public void setSecret(String value) { secret = value; }
        public String peekSecret() { return secret; }
    }

    public static final class PlainBean {
        private String name;
        private String note;

        public String getName() { return name; }
        public void setName(String value) { name = value; }
        public String getNote() { return note; }
        public void setNote(String value) { note = value; }
        public String getReadOnly() { return "readable"; }
    }

    private static void require(boolean condition, String message) {
        if (!condition) { throw new AssertionError(message); }
    }

    private static void run(String name, Runnable test) {
        try {
            test.run();
            System.out.println("PASS " + name);
        } catch (Throwable error) {
            failures++;
            System.err.println("FAIL " + name + ": " + error);
            error.printStackTrace(System.err);
        }
    }

    public static void main(String[] args) {
        run("toMap ignores a setter-only property", () -> {
            WriteOnlyBean bean = new WriteOnlyBean();
            bean.setName("alice");
            bean.setSecret("private");
            Map<?, ?> map = BeanUtil.toMap(bean);
            require("alice".equals(map.get("name")), "readable name lost");
            require(map.containsKey("note") && map.get("note") == null, "null getter value lost");
            require(!map.containsKey("secret") && !map.containsKey("class"), "unreadable or class property exposed");
            require(map.size() == 2, "unexpected property set: " + map);
        });
        run("bean-to-map copy keeps readable fields and ignore list", () -> {
            WriteOnlyBean bean = new WriteOnlyBean();
            bean.setName("alice");
            bean.setNote("ignored");
            Map<String, Object> map = new HashMap<>();
            map.put("existing", 7);
            BeanUtil.copyProperties(bean, map, "note");
            require("alice".equals(map.get("name")), "readable field not copied");
            require(Integer.valueOf(7).equals(map.get("existing")), "existing entry changed");
            require(!map.containsKey("note") && !map.containsKey("secret"), "excluded field copied");
        });
        run("copyNotNullProperties reaches real assignment with setter-only beans", () -> {
            WriteOnlyBean source = new WriteOnlyBean();
            source.setName("updated");
            source.setSecret("source secret");
            WriteOnlyBean target = new WriteOnlyBean();
            target.setName("old");
            target.setNote("retained");
            target.setSecret("target secret");
            BeanUtil.copyNotNullProperties(source, target);
            require("updated".equals(target.getName()), "non-null value not copied");
            require("retained".equals(target.getNote()), "existing null-copy policy changed");
            require("target secret".equals(target.peekSecret()), "unreadable value was copied");
        });
        run("ordinary readable bean and read-only getter control", () -> {
            PlainBean bean = new PlainBean();
            bean.setName("plain");
            Map<?, ?> map = BeanUtil.toMap(bean);
            require("plain".equals(map.get("name")), "ordinary value changed");
            require("readable".equals(map.get("readOnly")), "read-only getter excluded");
            require(map.containsKey("note") && map.get("note") == null, "null value excluded");
            require(!map.containsKey("class") && map.size() == 3, "class filtering changed");
        });
        run("ordinary bean-to-map ignore control", () -> {
            PlainBean bean = new PlainBean();
            bean.setName("plain");
            bean.setNote("hidden");
            Map<String, Object> map = new HashMap<>();
            BeanUtil.copyProperties(bean, map, "note");
            require("plain".equals(map.get("name")) && !map.containsKey("note"), "ignore list changed");
            require("readable".equals(map.get("readOnly")), "read-only getter not copied");
        });
        run("throwing getter remains a real PropertyException control", () -> {
            try {
                BeanUtil.toMap(new ThrowingBean());
                throw new AssertionError("getter exception was hidden");
            } catch (com.highgo.cloud.exception.PropertyException expected) {
                require(expected.getCause() != null, "getter exception cause lost");
            }
        });
        if (failures != 0) { throw new AssertionError(failures + " cases failed"); }
        System.out.println("6 cases passed");
    }

    public static final class ThrowingBean {
        public String getValue() { throw new IllegalStateException("getter failure"); }
    }
}
