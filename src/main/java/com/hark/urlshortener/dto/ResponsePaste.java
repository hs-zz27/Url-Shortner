package com.hark.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePaste {
    private String shortCode;
    private String url;
    private boolean newlyCreated;
}
