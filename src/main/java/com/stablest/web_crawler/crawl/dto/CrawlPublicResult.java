package com.stablest.web_crawler.crawl.dto;

import java.util.List;

public class CrawlPublicResult {
    final private String id;
    final private String status;
    final private List<String> urls;

    public CrawlPublicResult(String id, String status, List<String> urls) {
        this.id = id;
        this.status = status;
        this.urls = urls;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getUrls() {
        return urls;
    }
}
