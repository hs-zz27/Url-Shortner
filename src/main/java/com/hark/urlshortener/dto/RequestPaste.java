package com.hark.urlshortener.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestPaste {
    @NotBlank(message = "text must not be empty")
    private String text;

    @Positive(message = "expiresInSeconds must be positive")
    @Max(value = 31_536_000, message = "expiresInSeconds cannot exceed 1 year")
    private Long expiresInSeconds;
}
