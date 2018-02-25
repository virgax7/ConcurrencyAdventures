package tests.AQS;

import coreClasses.AQS.MutexLock;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MutexLockTest {
    @Test
    public void testLockIsMutex() throws InterruptedException {
        final MutexLock lock = new MutexLock();
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        new Thread(() -> {
            lock.lock();
            try {
                latch1.countDown();
                latch2.await();
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.unlock();
        }).start();
        latch1.await();
        final long startTime = System.currentTimeMillis();
        latch2.countDown();
        lock.lock();
        final long endTime = System.currentTimeMillis();
        assertTrue(endTime - startTime >= 5000);
    }
}
