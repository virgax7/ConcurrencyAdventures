package gotchas;

import java.util.concurrent.*;

public class ThreadPoolExecutorRejection1 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final ExecutorService exec = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>() {
                    @Override
                    public boolean offer(Runnable runnable) {
                        System.out.println("offer " + Thread.currentThread().getName());
                        boolean result = super.offer(runnable);
                        System.out.println(result); // if offer fails here then it means the runnable is rejected :P
                        return result;
                    }

                    @Override
                    public Runnable take() throws InterruptedException {
                        System.out.println("take " + Thread.currentThread().getName());
                        Thread.sleep(300000);
                        System.out.println("doesn't reach here");
                        return super.take(); // only once its reached a point in the take method
                        // the will let offer succeed, will the offer to synchqueue succeed,
                        // but that's not the case since we sleep for so long and the exec.execute will make
                        // the ThreadPoolExecutor try to call offer through the synchqueue
                    }
                });
        final Future f = exec.submit(() -> System.out.println("1st " + Thread.currentThread().getName()));
        f.get();
        try {
            System.out.println("Reached here");
            exec.execute(() -> System.out.println("This should not reach"));
        } catch (RejectedExecutionException gotcha) {
            gotcha.printStackTrace();
            // did this surprise you? I mean the active threads is zero and completed tasks is 1, since f.get()
            // ensured this, but it doesn't mean that f.get() ensures successful task enqueueing
        }
        exec.shutdownNow();
    }
}
