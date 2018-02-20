package timed.executors;

import java.util.concurrent.*;

public class TimerDemoFuture {
    public static void main(String[] args) throws InterruptedException {
        final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        final CountDownLatch latch = new CountDownLatch(1);
        exec.schedule(() -> {
            System.out.println("beep");
            System.out.println("this ran in the thread : " + Thread.currentThread().getName());
            latch.countDown();
        }, 1000, TimeUnit.MILLISECONDS);
        exec.schedule(() -> System.out.println("This shouldn't be ran"), 100000, TimeUnit.MILLISECONDS);
        System.out.println("Non blocking");
        latch.await();
        exec.shutdownNow();
    }
}
