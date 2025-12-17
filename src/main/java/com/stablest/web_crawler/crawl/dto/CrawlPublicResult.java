package com.stablest.web_crawler.crawl.dto;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CrawlPublicResult that = (CrawlPublicResult) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public List<String> getUrls() {
        return urls;
    }
}
