package com.hark.urlshortener.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "paste")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paste {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_code", unique = true, nullable = false)
    private String shortCode;

    @NotNull
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at") // nullable by default → null = never expires
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Long views;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.views == null)
            this.views = 0L;
    }
}