import static java.lang.Class.forName;
import static java.lang.System.out;

public class TestMySpring {
    public static void main(String[] args) {
        testForname();
    }

    public static void testString() {
        System.out.println("sf.ls".replace(".l", "hh"));
    }

    public static void testClass() { out.println("ff".getClass() == "fs".getClass()); }

    public static void testForname() {
        try {
            Class it = Class.forName("annotations.MyInterface");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
