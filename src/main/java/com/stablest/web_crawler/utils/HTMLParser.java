package com.stablest.web_crawler.utils;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class HTMLParser {

    public static String toString(String urlString) {
        try {
            URL url = HTMLParser.class.getClassLoader().getResource(urlString);
            if (url == null) {
                return null;
            }
            return Files.readString(
                    Path.of(url.toURI())
            );
        } catch (Exception e) {
            return null;
        }
    }
}
