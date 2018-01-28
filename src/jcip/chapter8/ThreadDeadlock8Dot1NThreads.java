package jcip.chapter8;

import java.util.concurrent.*;

// N = 2 in our case
public class ThreadDeadlock8Dot1NThreads {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final Future<String> renderedPage = exec.submit(new RenderPageTaskDelegator());
        System.out.println(renderedPage.get());
        exec.shutdown();
    }

    static final ExecutorService exec = Executors.newFixedThreadPool(2);
    // if you had 3 threads to use then it won't deadlock, comment above and uncomment below to try
//    static final ExecutorService exec = Executors.newFixedThreadPool(3);

    public static class RenderPageTaskDelegator implements Callable<String> {
        public String call() throws Exception {
            final Future<String> page = exec.submit(new RenderPageTask());
            return page.get();
        }
    }

    public static class RenderPageTask implements Callable<String> {
        public String call() throws Exception {
            final Future<String> header = exec.submit(new HeaderThatNeverGetsRun());
            // no more threads the exec can spawn to finish the header.get()... so it deadlocks
            return header.get() + "\nbody\n";
        }
    }

    public static class HeaderThatNeverGetsRun implements Callable<String> {
        @Override
        public String call() {
            System.out.println("This will never print");
            return "header";
        }
    }
}
