package com.stablest.web_crawler.common.utils;

import java.util.Random;

public class AlphanumericGenerator {
    public static String generate(int size) {
        int leftLimit = 48;
        int rightLimit = 122;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(size)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static String generate() {
        return generate(8);
    }
}
