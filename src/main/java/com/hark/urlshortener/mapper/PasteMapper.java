package com.hark.urlshortener.mapper;

import org.springframework.stereotype.Component;

import com.hark.urlshortener.dto.RequestPaste;
import com.hark.urlshortener.dto.ResponsePaste;
import com.hark.urlshortener.model.Paste;

@Component
public class PasteMapper {

    public Paste toEntity(RequestPaste req, String code) {
        return Paste.builder()
                .shortCode(code)
                .text(req.getText())
                .build();
    }

    public ResponsePaste toResponse(Paste paste, String url, boolean newlyCreated) {
        return new ResponsePaste(paste.getShortCode(), url, newlyCreated);
    }
}