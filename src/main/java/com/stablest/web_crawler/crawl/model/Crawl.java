package com.stablest.web_crawler.crawl.model;

import java.net.URI;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class Crawl {

    public enum Status {
        ACTIVE("active"),
        DONE("done");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    static final private Pattern linkPattern = Pattern.compile(
            "<a\\b[^>]*?href\\s*=\\s*(?:['\"]([^'\"]+)['\"]|([^\\s>]+))",
            Pattern.CASE_INSENSITIVE
    );

    private Status status;
    final private AtomicInteger activeTasks;
    final private String id;
    final private String keyword;
    final private String lowerCaseKeyword;
    final private URI baseURL;
    final private Set<String> visited;
    final private Set<String> toVisit;
    final private Set<String> urlListMatched;

    public Crawl(String keyword, String id, URI baseURL) {
        this.keyword = keyword;
        this.id = id;
        this.baseURL = baseURL;
        lowerCaseKeyword = keyword.toLowerCase();
        status = Status.ACTIVE;
        urlListMatched = ConcurrentHashMap.newKeySet();
        visited = ConcurrentHashMap.newKeySet();
        toVisit = ConcurrentHashMap.newKeySet();
        toVisit.add(baseURL.toString());
        activeTasks = new AtomicInteger(0);
    }

    public String getId() {
        return id;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getLowerCaseKeyword() {
        return lowerCaseKeyword;
    }

    public URI getBaseURL() {
        return baseURL;
    }

    public Set<String> getVisited() {
        return visited;
    }

    public Set<String> getToVisit() {
        return toVisit;
    }

    public AtomicInteger getActiveTasks() {
        return activeTasks;
    }

    public Status getStatus() {
        return status;
    }

    public Set<String> getUrlListMatched() {
        return urlListMatched;
    }

    public Pattern getLinkPattern() {
        return linkPattern;
    }

    public void complete() {
        status = Status.DONE;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Crawl that = (Crawl) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "CrawlResult{" +
                "id='" + id + '\'' +
                ", keyword='" + keyword + '\'' +
                ", status=" + status +
                ", urlList=" + urlListMatched +
                '}';
    }
}
