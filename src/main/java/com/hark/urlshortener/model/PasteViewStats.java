package com.hark.urlshortener.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "paste_view_stats", uniqueConstraints = @UniqueConstraint(columnNames = { "short_code", "month" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasteViewStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_code", nullable = false)
    private String shortCode;

    @Column(nullable = false)
    private String month; // "2026-07"

    @Column(nullable = false)
    private long views;
}