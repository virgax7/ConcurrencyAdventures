package timed.timer;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

public class TimerDemo {
    public static void main(String[] args) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final TimerTask timerTask1 = new TimerTask() {
            @Override
            public void run() {
                System.out.println("beep");
                System.out.println("this ran in the thread : " + Thread.currentThread().getName());
                latch.countDown();
            }
        };
        final TimerTask timerTask2 = new TimerTask() {
            @Override
            public void run() {
                System.out.println("This shouldn't be ran :P because of timer.cancel");
            }
        };
        final Timer timer = new Timer();
        timer.schedule(timerTask1, 1000);
        timer.schedule(timerTask2, 100000);
        System.out.println("timer is non blocking- this ran in the thread : " + Thread.currentThread().getName());
        latch.await();
        timer.cancel(); // this terminates the timer discarding any currently scheduled TimerTasks
        timer.purge(); // clean up cancelled timer tasks from the timer queue and garbage collected
    }

}
