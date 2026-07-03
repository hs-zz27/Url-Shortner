package com.hark.urlshortener.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasteStatsResponse {
    private String shortCode;
    private long totalViews;
    private Map<String, Long> monthlyViews;
}
