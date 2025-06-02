package com.t1.snezhko.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Value("${app.transactions.cache-size}")
    private int cacheSize;

    @Bean
    public Cache<String, Deque<Instant>> ttlCache() {
        return Caffeine.newBuilder()
//                .expireAfterWrite(ttl, TimeUnit.SECONDS)
                .maximumSize(cacheSize)
                .build();
    }
}
