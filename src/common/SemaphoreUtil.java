package common;

import java.util.concurrent.Semaphore;

// pseudo methods and helper methods I use for Semaphores
public class SemaphoreUtil {
    public static void P(final Semaphore semaphore) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void V(final Semaphore semaphore) {
        semaphore.release();
    }

    public static void P(final Semaphore semaphore, final int i) {
        try {
            semaphore.acquire(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void V(final Semaphore semaphore, final int i) {
        semaphore.release(i);
    }
}
