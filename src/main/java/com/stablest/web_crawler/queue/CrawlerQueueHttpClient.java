package com.stablest.web_crawler.queue;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executors;

public class CrawlerQueueHttpClient {
    final private static HttpClient CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(60))
            .executor(Executors.newFixedThreadPool(16))
            .build();

    public static HttpClient getClient() {
        return CLIENT;
    }
}
