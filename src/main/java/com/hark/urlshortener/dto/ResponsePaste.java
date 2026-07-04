package com.hark.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePaste {
    private String shortCode;
    private String url;
    private boolean newlyCreated;
}
