package com.stablest.web_crawler;

import com.stablest.web_crawler.controller.CrawlController;
import com.stablest.web_crawler.exception.ExceptionHandler;
import com.stablest.web_crawler.exception.NotFoundException;
import com.stablest.web_crawler.exception.ValidationException;
import com.stablest.web_crawler.context.ApplicationContext;
import com.stablest.web_crawler.service.WorkerService;
import com.stablest.web_crawler.transformer.JsonTransformer;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        ApplicationContext.createContext(args);
        WorkerService.createWorkers(4);
        exception(ValidationException.class, ExceptionHandler::ValidationException);
        exception(RuntimeException.class, ExceptionHandler::RuntimeException);
        exception(NotFoundException.class, ExceptionHandler::NotFoundException);
        path("/crawl", () -> {
            get("/:id", CrawlController.getInstance()::getCrawlResult, new JsonTransformer());
            post("", CrawlController.getInstance()::createCrawl, new JsonTransformer());
        });
    }
}
