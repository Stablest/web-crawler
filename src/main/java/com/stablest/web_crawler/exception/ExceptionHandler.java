package com.stablest.web_crawler.exception;

import com.google.gson.Gson;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;

public class ExceptionHandler {
    final static private Gson gson = new Gson();

    public static void ValidationException(Exception exception, Request request, Response response) {
        String jsonResponse = gson.toJson(new APIError(exception.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY_422));
        response.type("application/json");
        response.body(jsonResponse);
    }

    public static void NotFoundException(Exception exception, Request request, Response response) {
        String jsonResponse = gson.toJson(new APIError(exception.getMessage(), HttpStatus.NOT_FOUND_404));
        response.type("application/json");
        response.body(jsonResponse);
    }

    public static void RuntimeException(Exception exception, Request request, Response response) {
        String jsonResponse = gson.toJson(new APIError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR_500));
        response.type("application/json");
        response.body(jsonResponse);
    }
}
