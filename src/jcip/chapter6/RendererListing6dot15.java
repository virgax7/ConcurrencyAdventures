package jcip.chapter6;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static common.SemaphoreUtil.P;
import static common.SemaphoreUtil.V;

public class RendererListing6dot15 {
    public static void main(String[] args) {
        final RendererListing6dot15 renderer = new RendererListing6dot15(Executors.newCachedThreadPool());
        String testString = "";
        for (int i = 0, j = 0; i < IMAGE_URL_COUNT; i++) {
            testString += "Some Text" + j++ + "\n";
            testString += "Some Text" + j++ + "\n";
            testString += "Some Text" + j++ + "\n";
            testString += "imageUrl:somepath" + j++ + "\n";
        }
        renderer.renderPage(testString);
        if (!executeOriginalCodePath) {
            P(imageSemaphore);
        }
        System.out.println(renderer.mockPage);
        renderer.executor.shutdown();
    }

    private final ExecutorService executor;
    private final List<String> mockPage = Collections.synchronizedList(new ArrayList<>());
    private static final int IMAGE_URL_COUNT = 4;
    private static Semaphore imageSemaphore = new Semaphore(-IMAGE_URL_COUNT + 1);
    private static final boolean executeOriginalCodePath = false;

    RendererListing6dot15(final ExecutorService executor) {
        this.executor = executor;
    }

    void renderPage(final String source) {
        final List<ImageInfo> info = scanForImageInfo(source);
        final CompletionService<ImageData> completionService = new ExecutorCompletionService<>(executor);
        info.stream().forEach(imageInfo -> completionService.submit(() -> imageInfo.downloadImage()));

        renderText(source);

        if (executeOriginalCodePath) {
            codePathOriginal(info, completionService);
        } else {
            for (int i = 0; i < info.size(); i++) {
                System.out.println("going");
                executor.execute(() -> {
                    try {
                        final Future<ImageData> f = completionService.take();  // throws InterruptedException
                        renderImage(f.get());
                        System.out.println(mockPage);
                        V(imageSemaphore);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private void codePathOriginal(final List<ImageInfo> info, final CompletionService<ImageData> completionService) {
        try {
            for (int i = 0; i < info.size(); i++) {
                System.out.println("going will only print once and then block");
                /*
                    "And by fetching results from the
                    CompletionService and rendering each image as soon as it is available, we can
                    give the user a more dynamic and responsive user interface."(Goetz. 130)
                 */
                final Future<ImageData> f = completionService.take();  // throws InterruptedException

//                // However, although the download is in parallel, the get will block which will stop the renderImage
                final ImageData imageData = f.get(); // throws InterruptedException, ExecutionException
                renderImage(imageData);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw launderThrowable(e.getCause());
        }
    }

    private void renderImage(final ImageData imageData) {
        mockPage.add("rendered " + imageData.getData());
    }

    private void renderText(final String source) {
        Arrays.stream(source.split("\n"))
                .filter(s -> s.contains("Text"))
                .forEach(s -> mockPage.add("rendered " + s.replaceAll(".*(Text\\d+)", "$1\n")));
    }

    private List<ImageInfo> scanForImageInfo(final String source) {
        return Arrays.stream(source.split("\n"))
                .filter(s -> s.matches("imageUrl:somepath\\d+"))
                .map(s -> new ImageInfo(s.replaceAll("imageUrl:(somepath\\d+)", "$1\n")))
                .collect(Collectors.toList());
    }

    private RuntimeException launderThrowable(final Throwable cause) {
        if (cause instanceof RuntimeException) {
            return (RuntimeException) cause;
        } else if (cause instanceof Error) {
            throw (Error) cause;
        } else {
            throw new IllegalStateException("Not unchecked", cause);
        }
    }
}

class ImageInfo {
    private final String imageUrl;

    ImageInfo(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    ImageData downloadImage() throws InterruptedException {
        // some image fetch and conversion to bytes...
        Thread.sleep(new Random().nextInt(3000) + 1000);
        return new ImageData("data for " + imageUrl);
    }
}

class ImageData {
    private final String data;

    ImageData(final String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

}
