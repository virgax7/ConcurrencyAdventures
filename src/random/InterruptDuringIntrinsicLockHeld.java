package random;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import static random.InterruptDuringIntrinsicLockHeld.latch;

public class InterruptDuringIntrinsicLockHeld {
    final static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws InterruptedException {
        final Foo foo = new Foo();
        final Thread thread = new Thread(() -> {
            try {
                foo.intrinsicLock();
            } catch (InterruptedException e) {
                System.out.println("interrupted");
            }
            foo.isLockReleased();
        });
        thread.start();
        latch.await();
        thread.interrupt();
    }
}

class Foo {
    synchronized void intrinsicLock() throws InterruptedException {
        System.out.println(this + "  entered");
        latch.countDown();
        new Semaphore(0).acquire();
        System.out.println("After Interrupted");
    }

    synchronized void isLockReleased() {
        System.out.println("Yes, the Semaphore acquire has been interrupted and the synchronized method released its lock");
    }
}
