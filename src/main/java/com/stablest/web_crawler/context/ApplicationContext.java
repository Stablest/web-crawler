package com.stablest.web_crawler.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;

public class ApplicationContext {
    private static ApplicationContext INSTANCE;
    private final String baseURL;

    private ApplicationContext(String[] args) {
        String prefix = "BASE_URL" + "=";
        Optional<String> optionalBaseURL = Arrays.stream(args)
                .filter(arg -> arg.startsWith(prefix))
                .map(arg -> arg.substring(prefix.length()))
                .findFirst();
        this.baseURL = optionalBaseURL.orElseThrow(() -> new IllegalStateException("BASE_URL must be provided."));
        INSTANCE = this;
        Logger logger = LoggerFactory.getLogger(ApplicationContext.class);
        logger.info("Application is crawling {}", this.baseURL);
    }

    public static void createContext(String[] args) {
        if (INSTANCE == null) {
            new ApplicationContext(args);
        }
    }

    public static ApplicationContext getInstance() {
        return INSTANCE;
    }

    public String getBaseURL() {
        return baseURL;
    }
}
