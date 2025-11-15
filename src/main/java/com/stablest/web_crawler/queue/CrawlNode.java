package com.stablest.web_crawler.queue;

import com.stablest.web_crawler.dto.Crawl;

public class CrawlNode {
    private final Crawl crawl;
    private final String url;

    public CrawlNode(Crawl crawl, String url) {
        this.crawl = crawl;
        this.url = url;
    }

    public Crawl getCrawl() {
        return crawl;
    }

    public String getUrl() {
        return url;
    }
}
