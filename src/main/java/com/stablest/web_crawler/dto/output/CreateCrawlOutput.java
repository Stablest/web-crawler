package com.stablest.web_crawler.dto.output;

public class CreateCrawlOutput {
    final private String id;

    public CreateCrawlOutput(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
