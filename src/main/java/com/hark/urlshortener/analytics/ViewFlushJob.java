package com.hark.urlshortener.analytics;

import com.hark.urlshortener.model.PasteViewStats;
import com.hark.urlshortener.repository.PasteViewRepository;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ViewFlushJob {

    private final StringRedisTemplate redis;
    private final PasteViewRepository pasteViewRepository;

    @Scheduled(fixedRate = 300_000)
    @SchedulerLock(name = "flushViewCounts", lockAtLeastFor = "1m", lockAtMostFor = "4m")
    @Transactional
    public void flush() {
        Set<String> keys = redis.keys("views:*"); // views:{code}:{month}
        if (keys == null)
            return;

        for (String key : keys) {
            String value = redis.opsForValue().getAndDelete(key);
            if (value == null)
                continue;
            long delta = Long.parseLong(value);

            String[] parts = key.split(":");
            String code = parts[1];
            String month = parts[2];

            int updated = pasteViewRepository.addViews(code, month, delta);
            if (updated == 0) {
                pasteViewRepository.save(PasteViewStats.builder()
                        .shortCode(code)
                        .month(month)
                        .views(delta)
                        .build());
            }
        }
    }
}