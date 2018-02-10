package random;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.IntStream.range;

public class ThrottlingBehaviorTest1 {
    public static void main(String[] args) throws InterruptedException {
        final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1));
        // already the default policy
//        threadPoolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        final ExecutorService exec = new PersonalExecutor(threadPoolExecutor);
        /*
            Most likely to output

            inner execute is called 1 times.
            Aborted
            inner execute is called 2 times.

            because the first runnable is handled by the 1/1 thread the executor can allocate(maxmiumPoolSize)
            and then the next one is queued and then the next one is aborted
            The submission usually happens faster than in  2 seconds and therefore aborted gets called first
            before the queue can hand off the runnable to the now available thread that took 2 seconds to finish the first runnable

            Basically, the point is that once aborted, the inner execute doesn't get to do anything for the third execute..
            because in the ThreadPoolExecutor's execute method it will run reject(command);
         */
        range(0, 3).forEach($ -> exec.execute(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        Thread.sleep(7000);
        exec.shutdownNow();
    }
}

class PersonalExecutor extends AbstractExecutorService {
    private final ExecutorService exec;
    private final AtomicInteger counter = new AtomicInteger(0);
    PersonalExecutor(final ExecutorService exec) { this.exec = exec; }
    @Override
    public void shutdown() {}
    @Override
    public List<Runnable> shutdownNow() {return exec.shutdownNow();}
    @Override
    public boolean isShutdown() { return false; }
    @Override
    public boolean isTerminated() { return false; }
    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException { return false; }


    @Override
    public void execute(final Runnable command) {
        try {
            exec.execute(() -> {
                System.out.println("inner execute is called " + counter.incrementAndGet() + " times.");
                command.run();
            });
        } catch (RejectedExecutionException e) {
            System.out.println("Aborted");
        }
    }
}
