package random;

public class ThreadJoinMillisecondsBehaviorTest {
    public static void main(String[] args) throws InterruptedException {
        final Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100000000000000000L; i++) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
            }
            System.out.println("exiting now");
        });
        t1.start();
        final long startTime = System.currentTimeMillis();
        t1.join(2000);
        final long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
        // join doesn't actually kill the thread and the JVM cannot exit
        // because t1 is not a daemon thread.

        // join actually just waits x milliseconds for t1 to end and then returns...
        // you can later kill it with an interrupt mechanism or something
        t1.interrupt();
    }
}
