package com.hark.urlshortener.mapper;

import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.hark.urlshortener.dto.PasteStatsResponse;

@Mapper(componentModel = "spring")
public interface StatsMapper {
    @Mapping(target = "shortCode", source = "shortcode")
    @Mapping(target = "totalViews", source = "total")
    @Mapping(target = "monthlyViews", source = "monthlyViews")
    PasteStatsResponse toResponse(String shortcode, Map<String, Long> monthlyViews, Long total);
}
