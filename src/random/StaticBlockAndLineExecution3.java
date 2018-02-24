package random;

import java.util.concurrent.CountDownLatch;

import static random.StaticBlockAndLineExecution3.latch;

/*
   "Static initializers are run by the JVM at class initialization time, after class loading
    but before the class is used by any thread. Because the JVM acquires a lock during initialization [JLS 12.4.2] and
    this lock is acquired by each thread at least once to ensure that the class has been loaded,
    memory writes made during static initialization are automatically visible to all threads."(Goetz 16.2.3)
 */
public class StaticBlockAndLineExecution3 {
    static CountDownLatch latch = new CountDownLatch(1);
    static Thread mainThread = Thread.currentThread();
    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> keepLooping()).start();
        new Thread(() -> keepLooping()).start();
        int x = AllThreadsStopper.threadStopper;
    }

    static void keepLooping() {
        while (true) {
            try {
                latch.await();
                /*@ mark*/ System.out.println("This will never print " + AllThreadsStopper.threadStopper);

                // however, if you add the three lines below, the haltAllThreadsAndNeverReturn would throw an Exception and return
                /*"A method returns to the code that invoked it when it completes
                all the statements in the method, reaches a return statement, or throws an exception (covered later), whichever occurs first."
                - https://docs.oracle.com/javase/tutorial/java/javaOO/returnvalue.html */
                // this will end up leaving threadStopper as is, where the default value was 0 to start with so it will print zero
                // of course you would need to comment out the line above that is marked with /*@ mark*/
//                mainThread.interrupt();
//                Thread.sleep(500);
//                System.out.println("This will print " + AllThreadsStopper.threadStopper);
            } catch (InterruptedException e) {}
            System.out.println("This is thread " + Thread.currentThread().getName());
        }
    }
}

class AllThreadsStopper {
    static int threadStopper;

    static {
        try {
            threadStopper = haltAllThreadsAndNeverReturn();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static int haltAllThreadsAndNeverReturn() throws InterruptedException {
        System.out.println("haltAllThreadsAndNeverReturn called but only once from main since its locked from " + Thread.currentThread().getName());
        latch.countDown();
        new CountDownLatch(1).await();
        return 10000;
    }
}
