package com.hark.urlshortener.job;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hark.urlshortener.repository.PasteRepository;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Component
@RequiredArgsConstructor
public class ExpiredPasteCleanupJob {
    private final PasteRepository repository;

    @Scheduled(fixedRate = 30_000)
    @SchedulerLock(name = "deleteExpiredPastes", lockAtLeastFor = "1m", lockAtMostFor = "4m")
    @Transactional
    public void cleanupExpiredPastes() {
        repository.deleteExpiredBefore(LocalDateTime.now());
    }
}
