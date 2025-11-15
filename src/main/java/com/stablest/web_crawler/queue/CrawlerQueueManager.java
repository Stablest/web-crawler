package com.stablest.web_crawler.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;

public class CrawlerQueueManager {
    static private final CrawlerQueueManager INSTANCE = new CrawlerQueueManager();
    final private Semaphore semaphore = new Semaphore(256, true);
    final private HttpClient httpClient = CrawlerQueueHttpClient.getClient();
    final private Logger logger = LoggerFactory.getLogger(CrawlerQueueManager.class);
    final private ScheduledExecutorService workers = Executors.newScheduledThreadPool(1);
    final private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    final private BlockingQueue<CrawlNode> queue = new LinkedBlockingQueue<>();
    private volatile Consumer<CrawlNode> processStartedListener = crawlNode -> {};
    private volatile Consumer<Crawl> processCompletedListener = crawl -> {};

    public CrawlerQueueManager() {
        for (int i = 0; i < 1; i++) {
            workers.submit(this::processLoop);
        }
    }

    static public CrawlerQueueManager getInstance() {
        return INSTANCE;
    }

    private static boolean isValidLink(String link) {
        return !link.contains(" ") && !link.contains("%w");
    }

    private static boolean isValidScheme(String scheme) {
        return scheme != null && scheme.startsWith("http");
    }

    private static boolean isSameHost(String host1, String host2) {
        return Objects.equals(host1, host2);
    }

    public int getQueueSize() {
        return queue.size();
    }

    public void setOnProcessStarted(Consumer<CrawlNode> processStarted) {
        if (processStarted == null) {
            throw new IllegalStateException("Process started callback cannot be null");
        }
        this.processStartedListener = processStarted;
    }

    public void setOnProcessCompleted(Consumer<Crawl> processCompleted) {
        if (processCompleted == null) {
            throw new IllegalStateException("Process completed callback cannot be null");
        }
        this.processCompletedListener = processCompleted;
    }

    public boolean enqueue(CrawlNode crawlNode) {
        return queue.offer(crawlNode);
    }

    private void processLoop() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                CrawlNode crawlNode = queue.take();
                process(crawlNode);
            }
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            } else {
                logger.error("PROCESS_LOOP::ERROR::NOT_HANDLED: {}", e.getMessage());
            }
        }
    }

    private void process(CrawlNode crawlNode) {
        processStartedListener.accept(crawlNode);
        Crawl crawl = crawlNode.crawl();
        String currentURL = crawlNode.url();
        if (!crawl.getVisited().add(currentURL)) {
            return;
        }
        if (!crawl.getToVisit().remove(currentURL)) {
            return;
        }
        crawl.getActiveTasks().incrementAndGet();
        try {
            semaphore.acquire();
            URI currentURI = URI.create(currentURL);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(currentURI)
                    .timeout(Duration.ofSeconds(60))
                    .GET()
                    .build();
            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> onCrawlAccepted(response, crawl, currentURL))
                    .orTimeout(60, TimeUnit.SECONDS)
                    .exceptionally((exception) -> onCrawlException(exception, crawl, currentURL))
                    .whenComplete((_v, exception) -> onCrawlComplete(crawl));
        } catch (Exception exception) {
            onCrawlComplete(crawl);
        }
    }

    private void onCrawlAccepted(HttpResponse<String> response, Crawl crawl, String currentURL) {
        String html = response.body();
        if (html.toLowerCase().contains(crawl.getLowerCaseKeyword())) {
            crawl.getUrlListMatched().add(currentURL);
        }
        Matcher matcher = crawl.getLinkPattern().matcher(html);
        while (matcher.find()) {
            String link = matcher.group(1);
            if (!isValidLink(link)) {
                logger.debug("INVALID::LINK {}", link);
                continue;
            }
            URI resolved = crawl.getBaseURL().resolve(link);
            String resolvedStr = resolved.toString();
            String scheme = resolved.getScheme();
            if (!isValidScheme(scheme)) {
                logger.debug("INVALID::SCHEME {}", scheme);
                continue;
            }
            String resolvedHost = resolved.getHost();
            if (!isSameHost(resolvedHost, crawl.getBaseURL().getHost())) {
                logger.debug("INVALID::HOST {}", resolvedHost);
                continue;
            }
            if (!crawl.getVisited().contains(resolvedStr) && crawl.getToVisit().add(resolvedStr)) {
                enqueue(new CrawlNode(crawl, resolvedStr));
            }
        }
    }

    private Void onCrawlException(Throwable exception, Crawl crawl, String currentURL) {
        logger.debug("TASK::FAILED {}\n {}", currentURL, exception.toString());
        if (exception instanceof TimeoutException) {
            enqueue(new CrawlNode(crawl, currentURL));
        }
        if (exception instanceof CompletionException) {
            scheduler.schedule(() -> enqueue(new CrawlNode(crawl, currentURL)), 15, TimeUnit.SECONDS);
        }
        crawl.getVisited().remove(currentURL);
        crawl.getToVisit().add(currentURL);
        return null;
    }

    private void onCrawlComplete(Crawl crawl) {
        semaphore.release();
        int activeTasks = crawl.getActiveTasks().decrementAndGet();
        if (activeTasks == 0 && crawl.getToVisit().isEmpty()) {
            logger.info("CRAWL::COMPLETE {}", crawl.getId());
            crawl.complete();
        }
        processCompletedListener.accept(crawl);
    }

    public void shutdown() {
        workers.shutdown();
        scheduler.shutdown();
    }
}
