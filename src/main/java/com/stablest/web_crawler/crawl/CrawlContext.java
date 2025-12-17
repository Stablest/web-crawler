package com.stablest.web_crawler.crawl;

import com.stablest.web_crawler.crawl.model.Crawl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class CrawlContext {
    private final Semaphore entriesRemaining;
    private final ConcurrentHashMap<String, Crawl> resultSet = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(CrawlContext.class);

    public CrawlContext(int maxResultSize) {
        this.entriesRemaining = new Semaphore(maxResultSize);
    }

    public Optional<Crawl> getResult(String key) {
        Crawl crawl = resultSet.get(key);
        if (crawl == null) {
            return Optional.empty();
        }
        return Optional.of(crawl);
    }

    public void putInResult(String key, Crawl value) {
        if (!entriesRemaining.tryAcquire()) {
            logger.debug("MAX_RESULT_SET_SIZE_REACHED");
            throw new IllegalStateException("Cannot add key-value: maximum size reached");
        }
        Crawl existing = resultSet.putIfAbsent(key, value);
        if (existing != null) {
            entriesRemaining.release();
            logger.debug("KEY_ALREADY_EXISTS");
            throw new IllegalStateException("Cannot add key-value: key already exists");
        }
    }

}
