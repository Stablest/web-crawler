package com.stablest.web_crawler.dto.output;

import java.util.Objects;

public class CreateCrawlOutput {
    final private String id;

    public CreateCrawlOutput(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        CreateCrawlOutput that = (CreateCrawlOutput) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
