package com.hark.urlshortener.ratelimit;

import java.util.List;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RateLimiter {
    private final StringRedisTemplate redis;
    private final RedisScript<Long> slidingWindowScript;

    private static final long LIMIT = 100;
    private static final long WINDOW_MS = 60_000;

    public boolean isAllowed(String clientId) {
        String key = "rate:" + clientId;
        long now = System.currentTimeMillis();
        Long result = redis.execute(
                slidingWindowScript,
                List.of(key),
                String.valueOf(now),
                String.valueOf(WINDOW_MS),
                String.valueOf(LIMIT),
                UUID.randomUUID().toString());

        return result != null && result == 1;
    }
}
