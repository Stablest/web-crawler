package com.stablest.web_crawler.crawl;

import com.stablest.web_crawler.crawl.model.Crawl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CrawlContext {
    private final int maxSetSize;
    private final ConcurrentHashMap<String, Crawl> resultSet = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(CrawlContext.class);

    public CrawlContext(int maxResultSize) {
        this.maxSetSize = maxResultSize;
    }

    public Optional<Crawl> getResult(String key) {
        Crawl crawl = resultSet.get(key);
        if (crawl == null) {
            return Optional.empty();
        }
        return Optional.of(crawl);
    }

    public void putInResult(String key, Crawl value) {
        resultSet.compute(key, (k, foundValue) -> {
            if (foundValue == null && resultSet.size() >= maxSetSize) {
                logger.error("MAX_RESULT_SET_SIZE_REACHED");
                throw new IllegalStateException("Cannot add key-value: maximum site reached");
            }
            return value;
        });
    }

}
