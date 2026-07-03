package com.hark.urlshortener.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestPaste {
    private String text;
    private Long expiresInSeconds;
}
