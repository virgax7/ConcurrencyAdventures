package web.crawling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OccurrencesWebCrawler {
    private final WebCrawlerExecutor exec;
    // Nthreads = Ncpu * Ucpu * (1 + W/C)
    private final int magicNumberOfThreads;
    private final double waitTimeOverComputeTime;
    // not too sure about this one...
    private static final double U_CPU = .5;

    private final long searchDurationInSeconds;
    private final String regexOfTextOccurrence;
    private final String startSubPage;
    private final String pageBaseUrl;
    private final String hrefCandidate;

    public ConcurrentMap<String, Integer> getCountMap() {
        return countMap;
    }

    private final ConcurrentMap<String, Integer> countMap = new ConcurrentHashMap<>();
    private final ConcurrentSkipListSet<String> visited = new ConcurrentSkipListSet<>();
    private final Consumer<String> occurrencesCounterConsumer = url -> countMap.put(url, countMap.getOrDefault(url, 0) + 1);

    private static final int QUEUE_LIMIT = 30000;

    public OccurrencesWebCrawler(final String regexOfTextOccurrence, final String startSubPage, final long searchDurationInSeconds, final String hrefCandidate, final String pageBaseUrl) throws IOException {
        this.regexOfTextOccurrence = regexOfTextOccurrence;
        this.startSubPage = startSubPage;
        this.searchDurationInSeconds = searchDurationInSeconds;
        this.hrefCandidate = hrefCandidate;
        this.pageBaseUrl = pageBaseUrl;

        waitTimeOverComputeTime = calculateWaitTimeOverComputeTime(pageBaseUrl + startSubPage, regexOfTextOccurrence);
        magicNumberOfThreads = (int) Math.round(Runtime.getRuntime().availableProcessors() * U_CPU * (1 + waitTimeOverComputeTime));
        exec = new WebCrawlerExecutor(new ThreadPoolExecutor(magicNumberOfThreads, magicNumberOfThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(QUEUE_LIMIT)));
    }

    private double calculateWaitTimeOverComputeTime(final String rootPage, final String searchStringRegex) throws IOException {
        final long startTime = System.nanoTime();
        final HttpURLConnection con = (HttpURLConnection) new URL(rootPage).openConnection();
        con.setRequestMethod("GET");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        final double waitTime = System.nanoTime() - startTime;
        final List<String> links = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            Matcher matcher = Pattern.compile(searchStringRegex).matcher(line);
            while (matcher.find()) {

            }
            matcher = Pattern.compile("href=(\"[^\"]\")").matcher(line);
            while (matcher.find()) {
                links.add(matcher.group(1));
            }
        }
        reader.close();

        final double totalComputeTime = System.nanoTime() - startTime;
        return waitTime / totalComputeTime;
    }

    public void start() {
        exec.execute(new BFSCrawlTask(occurrencesCounterConsumer, startSubPage, visited, regexOfTextOccurrence, hrefCandidate, pageBaseUrl, exec));
        try {
            TimeUnit.SECONDS.sleep(searchDurationInSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            exec.shutdownNow();
        }
    }
}

