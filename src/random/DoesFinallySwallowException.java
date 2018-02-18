package random;

public class DoesFinallySwallowException {
    // answer is no
    public static void main(String[] args) {
        final Thread t = new Thread(() -> {
            try {
                x();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
        t.interrupt();
    }

    public static void x() throws InterruptedException {
        try {
            Thread.sleep(5000);
        } finally {
        }
    }
}
