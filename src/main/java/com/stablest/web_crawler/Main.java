package com.stablest.web_crawler;

import com.stablest.web_crawler.common.ApplicationContext;
import com.stablest.web_crawler.common.exception.ExceptionHandler;
import com.stablest.web_crawler.common.exception.NotFoundException;
import com.stablest.web_crawler.common.exception.ValidationException;
import com.stablest.web_crawler.common.transformer.JsonTransformer;
import com.stablest.web_crawler.common.utils.AlphanumericGenerator;
import com.stablest.web_crawler.crawl.CrawlContext;
import com.stablest.web_crawler.crawl.CrawlController;
import com.stablest.web_crawler.crawl.CrawlService;
import com.stablest.web_crawler.crawl.queue.CrawlerQueueManager;

import static spark.Spark.*;

public class Main {

    static private void registerExceptions() {
        exception(ValidationException.class, ExceptionHandler::ValidationException);
        exception(RuntimeException.class, ExceptionHandler::RuntimeException);
        exception(NotFoundException.class, ExceptionHandler::NotFoundException);
    }

    static private ComponentRegistry registerComponents(String[] args) {
        ComponentRegistry registry = new ComponentRegistry();
        ApplicationContext applicationContext = new ApplicationContext(args);
        registry.register(ApplicationContext.class, applicationContext);
        CrawlContext crawlContext = registry.register(CrawlContext.class, new CrawlContext());
        CrawlerQueueManager crawlerQueueManager = registry.register(CrawlerQueueManager.class, new CrawlerQueueManager());
        AlphanumericGenerator alphanumericGenerator = registry.register(AlphanumericGenerator.class, new AlphanumericGenerator());
        CrawlService crawlService = registry
                .register(CrawlService.class, new CrawlService(applicationContext, crawlContext, crawlerQueueManager, alphanumericGenerator));
        registry.register(CrawlController.class, new CrawlController(crawlService));
        registry.register(JsonTransformer.class, new JsonTransformer());
        registry.freeze();
        return registry;
    }

    public static void main(String[] args) {
        registerExceptions();
        ComponentRegistry componentRegistry = registerComponents(args);
        CrawlController crawlController = componentRegistry.get(CrawlController.class);
        JsonTransformer jsonTransformer = componentRegistry.get(JsonTransformer.class);
        path("/crawl", () -> {
            get("/:id", crawlController::getCrawlResult, jsonTransformer);
            post("", crawlController::createCrawl, jsonTransformer);
        });
    }
}
