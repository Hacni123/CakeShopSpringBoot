package com.grokonez.jwtauthentication.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Cache Configuration
 * Configures caching using Caffeine
 * Demonstrates caching concept for performance optimization
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configure Caffeine Cache Manager
     * Sets up different cache configurations for different use cases
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .recordStats());

        // Define specific caches
        cacheManager.setCacheNames(Arrays.asList(
                "cakes",
                "categories",
                "users",
                "orders",
                "carts",
                "productDetails",
                "categoryDetails"
        ));

        return cacheManager;
    }

    /**
     * Cache configuration for cakes - 15 minutes TTL
     */
    @Bean(name = "cakesCacheManager")
    public CacheManager cakesCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("cakes");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(1000)
                .recordStats());
        return cacheManager;
    }

    /**
     * Cache configuration for categories - 30 minutes TTL (less frequent changes)
     */
    @Bean(name = "categoriesCacheManager")
    public CacheManager categoriesCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("categories");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .maximumSize(500)
                .recordStats());
        return cacheManager;
    }

    /**
     * Cache configuration for users - 20 minutes TTL
     */
    @Bean(name = "usersCacheManager")
    public CacheManager usersCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("users");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(20, TimeUnit.MINUTES)
                .maximumSize(500)
                .recordStats());
        return cacheManager;
    }
}

