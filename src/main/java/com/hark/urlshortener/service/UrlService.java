package com.hark.urlshortener.service;

import com.hark.urlshortener.dto.RequestPaste;
import com.hark.urlshortener.dto.ResponsePaste;
import com.hark.urlshortener.model.Paste;
import com.hark.urlshortener.repository.PasteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final PasteRepository repository;
    @Value("${app.base-url}")
    private String baseUrl;

    public ResponsePaste createPaste(RequestPaste req){
        Optional<Paste> existing = repository.findByText(req.getContent());
        if (existing.isPresent()) {
            Paste p = existing.get();
            return new ResponsePaste(p.getShortCode(), buildUrl(p.getShortCode()), false);
        }
        String code = generateCode();
        Paste paste = Paste.builder()
                .shortCode(code)
                .text(req.getContent())
                .build();
        repository.save(paste);
        return new ResponsePaste(code, buildUrl(code), true);
    }

    private String generateCode() {
        return UUID.randomUUID().toString().substring(0, 8);   // simple for now
    }

    private String buildUrl(String code) {
        return baseUrl + "/api/" + code;
    }

}
