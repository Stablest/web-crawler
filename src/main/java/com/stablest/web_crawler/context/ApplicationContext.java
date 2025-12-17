package com.stablest.web_crawler.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;

public class ApplicationContext {
    private final String baseURL;

    public ApplicationContext(String[] args) {
        String prefix = "BASE_URL" + "=";
        Optional<String> optionalBaseURL = Arrays.stream(args)
                .filter(arg -> arg.startsWith(prefix))
                .map(arg -> arg.substring(prefix.length()))
                .findFirst();
        this.baseURL = optionalBaseURL.orElseThrow(() -> new IllegalStateException("BASE_URL must be provided."));
        Logger logger = LoggerFactory.getLogger(ApplicationContext.class);
        logger.info("Application is crawling {}", this.baseURL);
    }

    public String getBaseURL() {
        return baseURL;
    }
}
