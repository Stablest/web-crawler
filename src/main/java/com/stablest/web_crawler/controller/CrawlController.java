package com.stablest.web_crawler.controller;

import com.stablest.web_crawler.dto.CrawlPublicResult;
import com.stablest.web_crawler.dto.input.CreateCrawlInput;
import com.stablest.web_crawler.dto.output.CreateCrawlOutput;
import com.stablest.web_crawler.service.CrawlService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

public class CrawlController {
    private static final CrawlController INSTANCE = new CrawlController();
    final private CrawlService crawlService;
    final private Gson gson;

    private CrawlController() {
        this.crawlService = CrawlService.getInstance();
        this.gson = new Gson();
    }

    public static CrawlController getInstance() {
        return INSTANCE;
    }

    public CreateCrawlOutput createCrawl(Request request, Response response) {
        CreateCrawlInput input =  gson.fromJson(request.body(), CreateCrawlInput.class);
        input.validate();
        return crawlService.createCrawl(input.getKeyword());
    }

    public CrawlPublicResult getCrawlResult(Request request, Response response) {
        String id = request.params("id");
        return crawlService.getCrawl(id);
    }
}
