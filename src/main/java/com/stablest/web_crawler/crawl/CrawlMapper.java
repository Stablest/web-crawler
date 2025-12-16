package com.stablest.web_crawler.crawl;

import com.stablest.web_crawler.crawl.dto.CrawlPublicResult;
import com.stablest.web_crawler.crawl.model.Crawl;

import java.util.ArrayList;

public class CrawlMapper {
    public static CrawlPublicResult toPublic(Crawl crawl) {
        return new CrawlPublicResult(crawl.getId(), crawl.getStatus().getValue(),
                new ArrayList<>(crawl.getUrlListMatched()));
    }
}
