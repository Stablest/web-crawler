package com.stablest.web_crawler;

import java.util.HashMap;
import java.util.Map;

public class ComponentRegistry {
    private boolean isFrozen = false;
    private final Map<Class<?>, Object> components = new HashMap<>();

    public <T> T register(Class<T> type, T instance) {
        if (isFrozen) {
            throw new IllegalStateException("Registry is frozen");
        }
        components.put(type, instance);
        return instance;
    }


    public <T> T get(Class<T> type) {
        Object component = components.get(type);
        return type.cast(component);
    }

    public void freeze() {
        isFrozen = true;
    }
}
