package com.ai.aitravelplanner.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmapConfig {
    @Value("${amap.key}")
    private String key;

    public String getKey() {
        return key;
    }
}