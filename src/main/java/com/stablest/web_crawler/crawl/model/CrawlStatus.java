package com.stablest.web_crawler.crawl.model;

public enum CrawlStatus {
    ACTIVE("active"),
    DONE("done");

    private final String value;

    CrawlStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}