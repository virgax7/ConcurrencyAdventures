package random;

public class LaunderThrowableTest {
    public static void main(String[] args) {
        throw launderThrowable(new Throwable());
        /*
            Exception in thread "main" java.lang.IllegalStateException: Not unchecked
                at random.LaunderThrowableTest.launderThrowable(LaunderThrowableTest.java:22)
                at random.LaunderThrowableTest.main(LaunderThrowableTest.java:5)
            Caused by: java.lang.Throwable
                ... 1 more
         */
//        System.out.println("unreachable");
    }

    private static RuntimeException launderThrowable(final Throwable cause) {
        if (cause instanceof RuntimeException) {
            return (RuntimeException) cause;
        } else if (cause instanceof Error) {
            throw (Error) cause;
        } else {
            throw new IllegalStateException("Not unchecked", cause);
        }
    }
}
