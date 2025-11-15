package com.stablest.web_crawler.service;

import com.stablest.web_crawler.dto.Crawl;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {
    private static ApplicationContext INSTANCE;
    private final ConcurrentHashMap<String, Crawl> resultSet = new ConcurrentHashMap<>();
    private final String baseURL;

    private ApplicationContext(String[] args) {
        String prefix = "BASE_URL" + "=";
        Optional<String> optionalBaseURL = Arrays.stream(args)
                .filter(arg -> arg.startsWith(prefix))
                .map(arg -> arg.substring(prefix.length()))
                .findFirst();
        this.baseURL = optionalBaseURL.orElseThrow(() -> new IllegalStateException("BASE_URL must be provided."));
        System.out.println(this.baseURL);
        INSTANCE = this;
    }

    public static void createContext(String[] args) {
        if (INSTANCE == null) {
            new ApplicationContext(args);
        }
    }

    public static ApplicationContext getInstance() {
        return INSTANCE;
    }

    public ConcurrentHashMap<String, Crawl> getResultSet() {
        return resultSet;
    }

    public String getBaseURL() {
        return baseURL;
    }
}
