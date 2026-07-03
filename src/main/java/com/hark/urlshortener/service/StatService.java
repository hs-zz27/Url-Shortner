package com.hark.urlshortener.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.hark.urlshortener.dto.PasteStatsResponse;
import com.hark.urlshortener.mapper.StatsMapper;
import com.hark.urlshortener.model.Paste;
import com.hark.urlshortener.model.PasteViewStats;
import com.hark.urlshortener.repository.PasteRepository;
import com.hark.urlshortener.repository.PasteViewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatService {
    private final PasteViewRepository pasteViewRepository;
    private final PasteRepository pasteRepository;
    private final StatsMapper statsMapper;
    private final StringRedisTemplate redis;

    public PasteStatsResponse getStats(String code) {
        pasteRepository.findByShortCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paste not found"));
        List<PasteViewStats> list = pasteViewRepository.findByShortCode(code);
        Map<String, Long> map = new TreeMap<>();
        Long total = 0L;

        for (PasteViewStats stats : list) {
            Long currentMonthViews = stats.getViews();
            map.put(stats.getMonth(), currentMonthViews);
            total += currentMonthViews;
        }

        Set<String> liveKeys = redis.keys("views:" + code + ":*"); // use SCAN at scale
        if (liveKeys != null) {
            for (String key : liveKeys) {
                String month = key.substring(key.lastIndexOf(':') + 1);
                String live = redis.opsForValue().get(key);
                if (live != null) {
                    long liveCount = Long.parseLong(live);
                    map.merge(month, liveCount, Long::sum);
                    total += liveCount;
                }
            }
        }

        return statsMapper.toResponse(code, map, total);
    }

}
