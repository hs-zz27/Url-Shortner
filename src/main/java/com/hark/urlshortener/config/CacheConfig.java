package com.hark.urlshortener.config;

import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CacheConfig.class);

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException e, Cache c, Object k) {
                log.warn("Redis GET failed, falling back to DB: {}", e.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache c, Object k, Object v) {
                log.warn("Redis PUT failed: {}", e.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache c, Object k) {
                log.warn("Redis EVICT failed: {}", e.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache c) {
                log.warn("Redis CLEAR failed: {}", e.getMessage());
            }
        };
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        Map<String, RedisCacheConfiguration> configs = new HashMap<>();
        configs.put("pastes", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)));
        configs.put("stats", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)));

        return RedisCacheManager.builder(connectionFactory)
                .withInitialCacheConfigurations(configs)
                .build();
    }
}
