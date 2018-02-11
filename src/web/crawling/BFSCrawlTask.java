package web.crawling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BFSCrawlTask implements Runnable {
    private final Consumer<String> otherTask;
    private final ConcurrentSkipListSet<String> visited;
    private final String searchStringRegex;
    private final String hrefValueCandidate;
    private final ExecutorService exec;
    private final String pageBaseUrl;
    private final String fullUrl;

    public String getFullUrl() {
        return fullUrl;
    }

    public BFSCrawlTask(final Consumer<String> otherTask, final String urlSubPath, final ConcurrentSkipListSet<String> visited,
                        final String searchStringRegex, final String hrefValueCandidate, final String pageBaseUrl, final ExecutorService exec) {
        this.otherTask = otherTask;
        this.visited = visited;
        this.searchStringRegex = searchStringRegex;
        this.hrefValueCandidate = hrefValueCandidate;
        this.pageBaseUrl = pageBaseUrl;
        this.fullUrl = pageBaseUrl + urlSubPath;
        this.exec = exec;
    }

    @Override
    public void run() {
        if (visited.add(fullUrl)) {
            HttpURLConnection con = null;
            try {
                BufferedReader reader = null;
                con = (HttpURLConnection) new URL(fullUrl).openConnection();
                try {
                    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        Matcher matcher = Pattern.compile(searchStringRegex).matcher(line);
                        while (matcher.find()) {
                            otherTask.accept(fullUrl);
                        }
                        matcher = Pattern.compile("href=\"(" + hrefValueCandidate + ")\"").matcher(line);
                        while (matcher.find()) {
                            exec.execute(new BFSCrawlTask(otherTask, matcher.group(1), visited, searchStringRegex, hrefValueCandidate, pageBaseUrl, exec));
                        }
                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                con.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
        }
    }
}
