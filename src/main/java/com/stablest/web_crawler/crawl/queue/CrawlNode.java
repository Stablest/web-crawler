package com.stablest.web_crawler.crawl.queue;

public record CrawlNode(Crawl crawl, String url, int retries) {

    public CrawlNode(Crawl crawl, String url) {
        this(crawl, url, 0);
    }

    public CrawlNode nextRetry() {
        return new CrawlNode(crawl, url, retries + 1);
    }
}
