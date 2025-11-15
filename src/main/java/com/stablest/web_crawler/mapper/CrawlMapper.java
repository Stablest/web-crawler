package com.stablest.web_crawler.mapper;

import com.stablest.web_crawler.dto.CrawlPublicResult;
import com.stablest.web_crawler.dto.Crawl;

import java.util.ArrayList;

public class CrawlMapper {
    public static CrawlPublicResult toPublic(Crawl crawl) {
        return new CrawlPublicResult(crawl.getId(), crawl.getStatus().getValue(),
                new ArrayList<>(crawl.getUrlListMatched()));
    }
}
