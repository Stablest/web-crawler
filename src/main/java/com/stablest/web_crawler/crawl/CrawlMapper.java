package com.stablest.web_crawler.crawl;

import com.stablest.web_crawler.crawl.dto.CrawlPublicResult;
import com.stablest.web_crawler.crawl.model.Crawl;

public class CrawlMapper {
    public static CrawlPublicResult toPublic(Crawl crawl) {
        return new CrawlPublicResult(crawl.getId(), crawl.getStatus().getValue(), crawl.getUrlListMatched());
    }
}
