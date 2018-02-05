package random;

import java.util.concurrent.*;

import static random.ShutDownNowBehaviorTest1.exec;
import static random.ShutDownNowBehaviorTest1.isShutDown;

// Let's test to see if the normal behavior(whether the executor actually rejects tasks happens)
// a RejectedExecutionException should be thrown
public class ShutDownNowBehaviorTest1 {
    static final ExecutorService exec = Executors.newCachedThreadPool();
    static volatile boolean isShutDown = false;

    public static void main(String[] args) throws InterruptedException {
        exec.execute(new InfiniteRunnable1());
        exec.shutdownNow();
        isShutDown = true;
    }
}

class InfiniteRunnable1 implements Runnable {
    @Override
    public void run() {
        System.out.println("This should only print once");
        while (!isShutDown) {
            //spin
        }
        exec.execute(new InfiniteRunnable1());
    }
}
