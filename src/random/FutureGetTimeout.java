package random;

import java.util.concurrent.*;

public class FutureGetTimeout {
    public static void main(String[] args) throws InterruptedException {
        final ExecutorService exec = Executors.newSingleThreadExecutor();
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        final Future<?> future = exec.submit(() -> {
            try {
                latch1.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Just like thread join, this does not actually interrupt the thread, but a subsequent call to cancel will");
            latch2.countDown();
        });
        try {
            future.get(3, TimeUnit.SECONDS); // does not throw interrupt after 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            latch1.countDown();
            e.printStackTrace();
        } finally {
//            future.cancel(true); this guy sends an interrupt just like shutdownNow or join on a thread
        }
        latch2.await();
        exec.shutdownNow();
    }
}
