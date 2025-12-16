package com.stablest.web_crawler.crawl;

import com.google.gson.Gson;
import com.stablest.web_crawler.crawl.dto.CrawlPublicResult;
import com.stablest.web_crawler.crawl.dto.input.CreateCrawlInput;
import com.stablest.web_crawler.crawl.dto.output.CreateCrawlOutput;
import spark.Request;
import spark.Response;

public class CrawlController {
    final private CrawlService crawlService;
    final private Gson gson = new Gson();

    public CrawlController(CrawlService crawlService) {
        this.crawlService = crawlService;
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
