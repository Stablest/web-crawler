package com.stablest.web_crawler.transformer;
import com.google.gson.Gson;
import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer {
    final private Gson gson = new Gson();

    @Override
    public String render(Object model) {
        return gson.toJson(model);
    }
}