package jcip.chapter8;

import java.util.concurrent.*;

public class ThreadDeadlock8Dot1 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final Future<String> renderedPage = exec.submit(new RenderPageTask());
        // will eventually deadlock after calling get on renderedPage
        System.out.println(renderedPage.get());
        exec.shutdown();
    }

    static final ExecutorService exec = Executors.newSingleThreadExecutor();

    public static class RenderPageTask implements Callable<String> {
        public String call() throws Exception {
            final Future<String> header = exec.submit(new HeaderThatNeverGetsRun());
            // deadlock actually happens here on the call to get...
            // this is because there is already a 1/1 thread used when submitting the new RenderPageTask
            // to the executor and now we are using that same executor to submit another task
            // and return something from that task.

            // However, if you don't specifically call get, under the hood, it will just see that there is already 1/1
            // threads used, so not run HeaderThatNeverGetsRun.call(), so it will print the "This will never print" and return the header to the header
            // which would be useless in this case, since you aren't doing anything with the header after return "\nbody\n" returned
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

