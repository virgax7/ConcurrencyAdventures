package random;

public class EvalOrder {
    // the point is, methods always evaluate left to right before any other operations take place
    public static void main(String[] args) {
        final String result1 = x() + (y() + (z()));
        System.out.println(result1);

        final int result2 = a() + (b() * (c()));
        System.out.println(result2);
    }

    private static int a() {
        System.out.println("a is first");
        return 1;
    }

    private static int b() {
        System.out.println("b is second");
        return 2;
    }

    private static int c() {
        System.out.println("c is third");
        return 1;
    }

    private static String x() {
        System.out.println("x is first");
        return "head-";
    }

    private static String y() {
        System.out.println("y is second");
        return "body-";
    }

    private static String z() {
        System.out.println("z is third");
        return "tail";
    }

}
