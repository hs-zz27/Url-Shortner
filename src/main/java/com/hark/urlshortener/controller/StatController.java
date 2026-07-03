package com.hark.urlshortener.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.hark.urlshortener.dto.PasteStatsResponse;
import com.hark.urlshortener.service.StatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StatController {

    private final StatService statsService;

    @GetMapping("/{code}/stats")
    public ResponseEntity<PasteStatsResponse> getStats(@PathVariable String code) {
        PasteStatsResponse res = statsService.getStats(code);
        return res != null ? ResponseEntity.ok(res) : ResponseEntity.notFound().build();
    }
}
