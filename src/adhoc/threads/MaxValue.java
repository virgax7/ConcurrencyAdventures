package adhoc.threads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

import static common.SemaphoreUtil.P;
import static common.SemaphoreUtil.V;

/*
    found @
    http://www.cs.carleton.edu/faculty/dmusican/cs348/java_multi/

    Description:
    Max value
    Write a program called MaxValue.java that finds the maximum value in an array of ints using 4 threads.
    Your main should be similar as the one in the above-linked SumThread example, though you should construct your array of random numbers instead of increasing numbers.
    You may assume in your threaded code that the array has at least 4 elements.
 */
public class MaxValue {
    // here I distort the description a bit, as I assume that the main thread does not count as a thread although in reality it does...
    public static void main(String[] args) {
        final long startTime = System.nanoTime();
        for (int $ = 0; $ < 100000; $++) {
            final int[] array = new int[1000];
            Arrays.fill(array, new Random().nextInt(10000));
            final List<MaxValueThread> maxValueThreads = new ArrayList<>();
            final int evenWorkPortion = array.length / 4;

            for (int i = 0; i < 4; i++) {
                final int low = i * evenWorkPortion;
                final int high = low + evenWorkPortion;
                maxValueThreads.add(new MaxValueThread(low, high, array));
            }
            maxValueThreads.stream().forEach(maxValueThread -> maxValueThread.start());

            final int multiThreadResult = maxValueThreads.stream().mapToInt(thread -> thread.getMax()).max().getAsInt();
            final int serialResult = Arrays.stream(array).max().getAsInt();
            if (multiThreadResult != serialResult) {
                throw new AssertionError("oops, multiThreadResult was " + multiThreadResult + " and serialResult was " + serialResult);
            }
        }
        final long endTime = System.nanoTime();
        System.out.println("took " + (endTime - startTime) + " that is " + (endTime - startTime + "").length() + " digits");
    }
}

class MaxValueThread extends Thread {
    private int max;
    private final int low, high;
    private final int[] array;
    // can also use a latch, but semaphores is my weapon of choice for a lot of things
    private final Semaphore gate = new Semaphore(0);

    MaxValueThread(final int low, final int high, final int[] array) {
        this.low = low;
        this.high = high;
        this.array = array;
    }

    @Override
    public void run() {
        for (int i = low; i < high; i++) {
            max = Math.max(array[i], max);
        }
        V(gate);
    }

    public int getMax() {
        // Memory consistency effects: Actions in a thread prior to calling a "release" method such as release()
        // happen-before actions following a successful "acquire" method such as acquire() in another thread.
        // thus "private int max" does not need to be volatile :)
        P(gate);
        return max;
    }
}

