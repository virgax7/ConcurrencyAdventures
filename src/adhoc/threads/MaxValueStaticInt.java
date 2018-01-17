package adhoc.threads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

// Same as adhoc.threads.MaxValue.java, but a sillier approach of making more state...(yay, bad practice)
// but in case one has to approach a rather state-bound written code written from someone else, might as well be not scared
public class MaxValueStaticInt {
    private static int max;
    private static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        final long startTime = System.nanoTime();
        for (int $ = 0; $ < 100000; $++) {
            max = 0;
            final int[] array = new int[1000];
            Arrays.fill(array, new Random().nextInt(10000));
            final int evenWorkPortion = array.length / 4;

            final List<Thread> maxValueThreads = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                final int low = i * evenWorkPortion;
                final int high = low + evenWorkPortion;
                maxValueThreads.add(new Thread(() -> {
                    for (int j = low; j < high; j++) {
                        // bad performance, but necessary because Math.max is check then act
                        // there is a race condition on max even if it is AtomicInteger, because check then act
                        synchronized (lock) {
                            max = Math.max(array[j], max);
                        }
                    }
                }));
            }

            for (int i = 0; i < 4; i++) {
                maxValueThreads.get(i).start();
            }
            // When a thread terminates and causes a Thread.join in another thread to return,
            // then all the statements executed by the terminated thread have a happens-before relationship with all the statements following the successful join.
            // The effects of the code in the thread are now visible to the thread that performed the join.
            for (int i = 0; i < 4; i++) {
                maxValueThreads.get(i).join();
            }

            final int serialMax = Arrays.stream(array).max().getAsInt();
            if (max != serialMax) {
                throw new AssertionError("oops, sharedMax was " + max + " and serialMax was " + serialMax);
            }
        }
        final long endTime = System.nanoTime();
        System.out.println("took " + (endTime - startTime) + " that is " + (endTime - startTime + "").length() + " digits");
    }
}
