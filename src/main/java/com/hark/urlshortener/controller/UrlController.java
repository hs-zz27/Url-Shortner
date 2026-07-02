package com.hark.urlshortener.controller;

import com.hark.urlshortener.dto.RequestPaste;
import com.hark.urlshortener.dto.ResponsePaste;
import com.hark.urlshortener.service.UrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
//import java.net.URI;

@RestController
public class UrlController {
    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<ResponsePaste> shortenUrl(@RequestBody RequestPaste req) {
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

}
