package com.stablest.web_crawler;

import com.stablest.web_crawler.common.ApplicationContext;
import com.stablest.web_crawler.common.ComponentRegistry;
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
    static private final String URL_ARG = "url=";
    static private final String MAX_SET_SIZE_ARG = "max-set-size=";
    static private final int DEFAULT_SET_SIZE = 10;

    static private ApplicationContext parseArgumentsIntoApplicationContext(String[] args) {
        String url = null;
        int maxSetSize = DEFAULT_SET_SIZE;
        for (String arg : args) {
            if (arg.startsWith(URL_ARG)) {
                url = arg.substring(URL_ARG.length());
                continue;
            }
            if (arg.startsWith(MAX_SET_SIZE_ARG)) {
                String valueAsString = arg.substring(MAX_SET_SIZE_ARG.length());
                try {
                    maxSetSize = valueAsString.isEmpty() ? DEFAULT_SET_SIZE : Integer.parseInt(valueAsString);
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException("max-set-size must be a valid a number", exception);
                }
            }
        }
        return new ApplicationContext(url, maxSetSize);
    }

    static private void registerExceptions() {
        exception(ValidationException.class, ExceptionHandler::ValidationException);
        exception(RuntimeException.class, ExceptionHandler::RuntimeException);
        exception(NotFoundException.class, ExceptionHandler::NotFoundException);
    }

    static private ComponentRegistry registerComponents(ApplicationContext applicationContext) {
        ComponentRegistry registry = new ComponentRegistry();
        registry.register(ApplicationContext.class, applicationContext);
        CrawlContext crawlContext = registry.register(CrawlContext.class, new CrawlContext(applicationContext.maxSetSize()));
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
        ApplicationContext applicationContext = parseArgumentsIntoApplicationContext(args);
        ComponentRegistry componentRegistry = registerComponents(applicationContext);
        CrawlController crawlController = componentRegistry.get(CrawlController.class);
        JsonTransformer jsonTransformer = componentRegistry.get(JsonTransformer.class);
        path("/crawl", () -> {
            get("/:id", crawlController::getCrawlResult, jsonTransformer);
            post("", crawlController::createCrawl, jsonTransformer);
        });
    }
}
