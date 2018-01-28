package random;

import java.util.concurrent.*;

// The question is does it ever hit the finally block even if method returned?
public class Listing7Dot7 {
    public static void main(String[] args) throws InterruptedException {
        final BlockingQueue<Task> queue = new ArrayBlockingQueue<>(5);
        queue.add(new Task());
        final Task task = getNextTask(queue);
        System.out.println(task);
        System.out.println("Yes it hits finally block even if return is called");
    }

    static Task getNextTask(final BlockingQueue<Task> queue) {
        try {
            while (true) {
                try {
                    return queue.take();
                } catch (InterruptedException consume) {
                } finally {
                    System.out.println("inside inner finally block");
                }
            }
        } finally {
            System.out.println("inside outer finally block");
        }
    }

    static class Task implements Runnable {
        @Override
        public void run() {}
    }
}
