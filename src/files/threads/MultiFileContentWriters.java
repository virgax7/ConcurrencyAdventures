package files.threads;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class MultiFileContentWriters {
    // yes I'm lazy with exception handling
    public static void main(String[] args) throws InterruptedException, IOException {
        final int NUM_CPU = Runtime.getRuntime().availableProcessors();
        final int NUM_LINES = new Random().nextInt(2000) + 0;
        final int EVEN_WORK_COUNT = NUM_LINES / NUM_CPU;
        final CountDownLatch gate = new CountDownLatch(EVEN_WORK_COUNT > 0 ? NUM_CPU : 0);
        final ConcurrentMap<Integer, List<String>> map = new ConcurrentSkipListMap<>();

        for (int i = 0; i < NUM_CPU && EVEN_WORK_COUNT > 0; i ++) {
            final int finalI = i * EVEN_WORK_COUNT;
            new Thread(() -> {
                map.put(finalI, IntStream.range(finalI, finalI + EVEN_WORK_COUNT)
                        .mapToObj(num -> {
                            try {
                                // some heavy computation to get contents to write to file...
                                Thread.sleep(10);
                            } catch (InterruptedException consumer) {}
                            return num + "";
                        })
                        .collect(toList()));
                gate.countDown();
            }).start();
        }

        gate.await();

        final List<String> leftOver = new ArrayList<>();
        for (int i = NUM_LINES - EVEN_WORK_COUNT * NUM_CPU; i > 0; i--) {
            leftOver.add(NUM_LINES - i + "");
        }
        map.put(Integer.MAX_VALUE, leftOver);

        Files.write(Paths.get("result.txt"),
                map.entrySet().stream().map(entry -> entry.getValue()) .collect(toList()).stream().flatMap(list -> list.stream()).collect(toList()),
                Charset.defaultCharset());

        final List<String> lines = Files.lines(Paths.get("result.txt")).collect(toList());
        if (lines.size() != NUM_LINES) {
            System.out.println(lines.size() +  " " + NUM_LINES);
            throw new AssertionError("result.txt is incorrect1");
        }
        for (int i = 1; i < lines.size(); i++) {
            if (Integer.valueOf(lines.get(i)) <= Integer.valueOf(lines.get(i - 1))) {
                throw new AssertionError("result.txt is incorrect2");
            }
        }
        Files.delete(Paths.get("result.txt"));
    }
}
