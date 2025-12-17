package com.stablest.web_crawler.crawl;

import com.stablest.web_crawler.common.ApplicationContext;
import com.stablest.web_crawler.common.exception.NotFoundException;
import com.stablest.web_crawler.common.utils.AlphanumericGenerator;
import com.stablest.web_crawler.crawl.dto.CrawlPublicResult;
import com.stablest.web_crawler.crawl.dto.output.CreateCrawlOutput;
import com.stablest.web_crawler.crawl.model.Crawl;
import com.stablest.web_crawler.crawl.model.CrawlNode;
import com.stablest.web_crawler.crawl.queue.CrawlerQueueManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class CrawlService {
    final private Logger logger = LoggerFactory.getLogger(CrawlService.class);
    final private CrawlContext crawlContext;
    final private ApplicationContext applicationContext;
    final private CrawlerQueueManager crawlerQueueManager;

    public CrawlService(ApplicationContext applicationContext, CrawlContext crawlContext, CrawlerQueueManager crawlerQueueManager) {
        this.applicationContext = applicationContext;
        this.crawlContext = crawlContext;
        this.crawlerQueueManager = crawlerQueueManager;
    }

    public CrawlPublicResult getCrawl(String id) {
        logger.info("GET::CRAWL {}", id);
        Crawl crawl = crawlContext.getResult(id).orElseThrow(() -> new NotFoundException("Crawl id not found."));
        return CrawlMapper.toPublic(crawl);
    }

    public CreateCrawlOutput createCrawl(String keyword) {
        logger.info("CREATE::CRAWL {}", keyword);
        String id = AlphanumericGenerator.generate();
        URI baseURI = URI.create(applicationContext.getBaseURL());
        Crawl crawl = new Crawl(keyword, id, baseURI);
        crawlContext.putInResult(id, crawl);
        crawlerQueueManager.process(new CrawlNode(crawl, baseURI.toString()));
        return new CreateCrawlOutput(id);
    }
}

