package com.hark.urlshortener.controller;

import com.hark.urlshortener.analytics.ViewAnalyticsService;
import com.hark.urlshortener.dto.PasteStatsResponse;
import com.hark.urlshortener.dto.RequestPaste;
import com.hark.urlshortener.dto.ResponsePaste;
import com.hark.urlshortener.service.UrlService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
//import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;
    private final ViewAnalyticsService viewAnalyticsService;

    @PostMapping("/shorten")
    public ResponseEntity<ResponsePaste> shortenUrl(@Valid @RequestBody RequestPaste req) {
        if (req.getText() == null || req.getText().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        ResponsePaste body = urlService.createPaste(req);
        if (body.isNewlyCreated()) {
            // return ResponseEntity.created(URI.create("/api/" +
            // body.getShortCode())).body(body);
            return ResponseEntity.status(HttpStatus.CREATED).body(body);
        } else
            return ResponseEntity.ok(body);
    }

    // cacheable moved to service
    @GetMapping("/{code}")
    public ResponseEntity<String> getPaste(@PathVariable String code) {
        String content = urlService.getPasteContent(code);
        viewAnalyticsService.recordView(code);
        return ResponseEntity.ok(content);
    }

    @DeleteMapping("/{code}")
    @CacheEvict(cacheNames = "pastes", key = "#code")
    public ResponseEntity<String> deletePaste(@PathVariable String code) {
        urlService.deletePaste(code);
        return ResponseEntity.ok("Paste deleted");
    }

}
