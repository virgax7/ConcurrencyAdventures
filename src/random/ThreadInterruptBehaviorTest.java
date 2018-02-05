package random;

public class ThreadInterruptBehaviorTest {
    public static void main(String[] args) throws InterruptedException {
        final Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100000000000000000L; i++) {
            }
            System.out.println("Would this print immediately after interruption?");
            System.out.println("Nope, interrupt merely sets a flag indicating whether its interrupted");
            System.out.println("It's up to you how you deal with that interruption...");
        });
        // uncomment to see this in action
//        t1.start();
        t1.interrupt();

        // Here is one that will actually stop the loop in the thread by using a polling system
        final Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100000000000000000L; i++) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
            }
            // calling isInterrupted clears the interruption status so we reset it
            Thread.currentThread().interrupt();
        });
        t2.start();
        t2.interrupt();
        System.out.println(t2.isInterrupted());
    }
}
