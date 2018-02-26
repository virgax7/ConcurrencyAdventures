package random;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class WhichThreadRunsCyclicBarrierAction {
    // The thread that releases the barrier runs the action
    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        final CyclicBarrier barrier = new CyclicBarrier(3, () -> System.out.println("The thread that ran this is " + Thread.currentThread().getName()));
        System.out.println(barrier.getNumberWaiting());

        new Thread(() -> {
            try {
                System.out.println("This is thread 1:  " + Thread.currentThread().getName());
                barrier.await();
            } catch (Exception e) {}
        }).start();

        new Thread(() -> {
            try {
                System.out.println("This is thread 2:  " + Thread.currentThread().getName());
                while (barrier.getNumberWaiting() != 2) {
                    // spin
                }
                barrier.await();
            } catch (Exception e) {}
        }).start();

        Thread.sleep(1000);
        System.out.println("This is main thread:  " + Thread.currentThread().getName());
        barrier.await();
    }
}
