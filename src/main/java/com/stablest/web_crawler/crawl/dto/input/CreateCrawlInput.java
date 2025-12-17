package com.stablest.web_crawler.crawl.dto.input;

import com.stablest.web_crawler.common.exception.ValidationException;

public class CreateCrawlInput {
    final private String keyword;

    public CreateCrawlInput(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void validate() {
        if (keyword == null) {
            throw new ValidationException("Keyword cannot be null.");
        }
        if (keyword.length() < 4) {
            throw new ValidationException("Keyword must be at least 4 characters long.");
        }
        if (keyword.length() > 32) {
            throw new ValidationException("Keyword must be at most 32 characters long.");
        }
    }

}
