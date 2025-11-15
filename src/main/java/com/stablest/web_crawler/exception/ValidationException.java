package com.stablest.web_crawler.exception;

public class ValidationException extends RuntimeException {
    public ValidationException() {
        super("A field is not valid.");
    }

    public ValidationException(String message) {
        super(message);
    }
}
