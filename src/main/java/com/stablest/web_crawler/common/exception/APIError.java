package com.stablest.web_crawler.common.exception;

public class APIError {
    final private String message;
    final private Integer status;

    public APIError(String message, Integer status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public Integer getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "ErrorObject{" +
                "message='" + message + '\'' +
                ", status=" + status +
                '}';
    }
}
