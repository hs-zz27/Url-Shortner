package com.hark.urlshortener.service;

import com.hark.urlshortener.dto.RequestPaste;
import com.hark.urlshortener.dto.ResponsePaste;
import com.hark.urlshortener.model.Paste;
import com.hark.urlshortener.repository.PasteRepository;
import com.hark.urlshortener.util.Snowflake;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final PasteRepository repository;
    private final Snowflake snowflake;

    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Value("${app.base-url}")
    private String baseUrl;

    public ResponsePaste createPaste(RequestPaste req) {
        Optional<Paste> existing = repository.findByText(req.getText());
        if (existing.isPresent()) {
            Paste p = existing.get();
            return new ResponsePaste(p.getShortCode(), buildUrl(p.getShortCode()), false);
        }
        String code = generateCode();
        Paste paste = Paste.builder()
                .shortCode(code)
                .text(req.getText())
                .build();
        repository.save(paste);
        return new ResponsePaste(code, buildUrl(code), true);
    }

    private String generateCode() {
        long id = snowflake.nextId();
        return toBase62(id);
    }

    private String buildUrl(String code) {
        return baseUrl + "/api/" + code;
    }

    private String toBase62(long id) {
        if (id == 0)
            return "0";
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            sb.append(BASE62.charAt((int) (id % 62)));
            id /= 62;
        }
        return sb.reverse().toString();
    }

}
