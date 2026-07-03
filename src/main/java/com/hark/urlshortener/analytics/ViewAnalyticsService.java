package com.hark.urlshortener.analytics;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViewAnalyticsService {

    private final StringRedisTemplate redis;

    @Async
    public void recordView(String code) {
        String month = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String key = "views:" + code + ":" + month;
        redis.opsForValue().increment(key);
        log.info("View recorded for code: {} in month: {}", code, month);
    }
}
