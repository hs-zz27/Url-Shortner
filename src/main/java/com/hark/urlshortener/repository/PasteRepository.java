package com.hark.urlshortener.repository;

import com.hark.urlshortener.model.Paste;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasteRepository extends JpaRepository<Paste, Long> {
    Optional<Paste> findByShortCode(String shortCode);
    Optional<Paste> findByText(String text);
}
