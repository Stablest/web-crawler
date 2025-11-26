package com.stablest.web_crawler.service;

import com.stablest.web_crawler.context.ApplicationContext;
import com.stablest.web_crawler.queue.Crawl;
import com.stablest.web_crawler.dto.CrawlPublicResult;
import com.stablest.web_crawler.dto.output.CreateCrawlOutput;
import com.stablest.web_crawler.exception.NotFoundException;
import com.stablest.web_crawler.mapper.CrawlMapper;
import com.stablest.web_crawler.queue.CrawlNode;
import com.stablest.web_crawler.queue.CrawlerQueueManager;
import com.stablest.web_crawler.utils.AlphanumericGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class CrawlService {
    final private static CrawlService INSTANCE = new CrawlService();
    final private ApplicationContext applicationContext;
    final private CrawlerQueueManager crawlerQueueManager;
    final private Logger logger = LoggerFactory.getLogger(CrawlService.class);

    private CrawlService() {
        applicationContext = ApplicationContext.getInstance();
        crawlerQueueManager = CrawlerQueueManager.getInstance();
    }

    public static CrawlService getInstance() {
        return INSTANCE;
    }

    public CrawlPublicResult getCrawl(String id) {
        logger.info("GET::CRAWL {}", id);
        Crawl crawl = applicationContext.getResultSet().get(id);
        if (crawl == null) {
            throw new NotFoundException("Crawl id not found.");
        }
        return CrawlMapper.toPublic(crawl);
    }

    public CreateCrawlOutput createCrawl(String keyword) {
        logger.info("CREATE::CRAWL {}", keyword);
        String id = AlphanumericGenerator.generate();
        URI baseURI = URI.create(applicationContext.getBaseURL());
        Crawl crawl = new Crawl(keyword, id, baseURI);
        applicationContext.getResultSet().put(id, crawl);
        crawlerQueueManager.process(new CrawlNode(crawl, baseURI.toString()));
        return new CreateCrawlOutput(id);
    }
}

