package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    private final static String HOST_NAME = "t1-service-module1:8080";

    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl("http://" + HOST_NAME).build();
    }
}
