package adhoc.executors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// Same problem as adhoc.threads.MaxValue.java, but using Executors
public class MaxValue {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final long startTime = System.nanoTime();

        for (int $ = 0; $ < 100000; $++) {
            final int[] array = new int[1000];
            Arrays.fill(array, new Random().nextInt(10000));
            final int evenWorkPortion = array.length / 4;

            final ExecutorService executorService = Executors.newCachedThreadPool();
            final List<Future<Integer>> maxes = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                final int low = i * evenWorkPortion;
                final int high = low + evenWorkPortion;
                maxes.add(executorService.submit(() -> {
                    int callableMax = 0;
                    for (int j = low; j < high; j++) {
                            callableMax = Math.max(array[j], callableMax);
                    }
                    return callableMax;
                }));
            }

            int max = 0;
            for (int i = 0; i < 4; i++) {
                max = Math.max(maxes.get(i).get(), max);
            }

            final int serialMax = Arrays.stream(array).max().getAsInt();
            if (max != serialMax) {
                throw new AssertionError("oops, sharedMax was " + max + " and serialMax was " + serialMax);
            }
            executorService.shutdown();
        }

        final long endTime = System.nanoTime();
        System.out.println("took " + (endTime - startTime) + " that is " + (endTime - startTime + "").length() + " digits");
    }
}
