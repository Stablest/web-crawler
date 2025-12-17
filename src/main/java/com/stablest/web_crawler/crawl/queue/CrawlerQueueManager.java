package com.stablest.web_crawler.crawl.queue;

import com.stablest.web_crawler.crawl.model.Crawl;
import com.stablest.web_crawler.crawl.model.CrawlNode;
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
    static private final int MAX_CONCURRENT_REQUESTS = 256;
    static private final int MAX_CRAWL_RETRIES = 4;
    static private final long RETRY_BASE_TIME = 15L;
    private final Logger logger = LoggerFactory.getLogger(CrawlerQueueManager.class);
    private final HttpClient httpClient = CrawlerQueueHttpClient.getClient();
    private final Semaphore semaphore = new Semaphore(MAX_CONCURRENT_REQUESTS, true);
    private final ExecutorService virtualTaskService = Executors.newVirtualThreadPerTaskExecutor();
    private final ScheduledExecutorService scheduledTaskService = Executors.newScheduledThreadPool(1);
    private volatile Consumer<CrawlNode> processStartedListener = crawlNode -> {};
    private volatile Consumer<Crawl> processCompletedListener = crawl -> {};

    private static boolean isValidLink(String link) {
        return !link.contains(" ") && !link.contains("%w");
    }

    private static boolean isValidScheme(String scheme) {
        return scheme != null && scheme.startsWith("http");
    }

    private static boolean isSameHost(String host1, String host2) {
        return Objects.equals(host1, host2);
    }

    private void onCrawlStarted(CrawlNode crawlNode) {
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
                    .orTimeout(60, TimeUnit.SECONDS)
                    .thenAcceptAsync(response -> onCrawlAccepted(response, crawl, currentURL), scheduledTaskService)
                    .exceptionally((exception) -> onCrawlException(exception, crawlNode, currentURL))
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
                process(new CrawlNode(crawl, resolvedStr));
            }
        }
    }

    private Void onCrawlException(Throwable exception, CrawlNode crawlNode, String currentURL) {
        logger.debug("TASK::FAILED {}\n {}", currentURL, exception.toString());
        if (exception instanceof TimeoutException || exception instanceof CompletionException) {
            retry(crawlNode);
        } else {
            logger.error("CRAWLER::EXCEPTION::NOT_HANDLED {}", exception.getMessage());
        }
        crawlNode.crawl().getVisited().remove(currentURL);
        crawlNode.crawl().getToVisit().add(currentURL);
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

    public void process(CrawlNode crawlNode) {
        virtualTaskService.submit(() -> onCrawlStarted(crawlNode));
    }

    public void retry(CrawlNode crawlNode) {
        if (crawlNode.retries() > MAX_CRAWL_RETRIES) {
            logger.error("CRAWL_MAXED_OUT_ERROR_RETRIES {}", crawlNode);
        } else {
            CrawlNode nextCrawlNode = crawlNode.nextRetry();
            long delay = nextCrawlNode.retries() * RETRY_BASE_TIME;
            scheduledTaskService.schedule(() -> process(nextCrawlNode), delay, TimeUnit.SECONDS);
        }
    }

    public void shutdown() {
        virtualTaskService.shutdown();
        scheduledTaskService.shutdown();
    }
}
