package com.hark.urlshortener.dto;

import lombok.Getter;

@Getter
public class ResponsePaste {
    private String shortCode;
    private String url;
    private boolean newlyCreated;
}
