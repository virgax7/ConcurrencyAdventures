package web.crawling;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

class WebCrawlerExecutor extends AbstractExecutorService {
    private final ExecutorService exec;
    private final List<String> abortedUrls = new ArrayList<>();

    public synchronized List<String> getAbortedUrls() {
        return abortedUrls;
    }

    WebCrawlerExecutor(final ExecutorService exec) {
        this.exec = exec;
    }

    @Override
    public void shutdown() {
        exec.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return exec.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return exec.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return exec.isTerminated();
    }

    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        return exec.awaitTermination(timeout, unit);
    }

    @Override
    public void execute(final Runnable runnable) {
        try {
            if (!Thread.currentThread().isInterrupted()) {
                exec.execute(() -> {
                    runnable.run();
                });
            } else {
                // write to a file or something
            }
        } catch (RejectedExecutionException e) {
            addAbortedUrlToList(runnable);
        }
    }

    private void addAbortedUrlToList(final Runnable runnable) {
        if (runnable instanceof BFSCrawlTask) {
            synchronized (this) {
                abortedUrls.add(((BFSCrawlTask) runnable).getFullUrl());
            }
        }
    }
}
