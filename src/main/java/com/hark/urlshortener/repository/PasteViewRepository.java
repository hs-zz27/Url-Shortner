package com.hark.urlshortener.repository;

import com.hark.urlshortener.model.PasteViewStats;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PasteViewRepository extends JpaRepository<PasteViewStats, Long> {
    @Modifying
    @Query("UPDATE PasteViewStats s SET s.views = s.views + :delta " +
            "WHERE s.shortCode = :code AND s.month = :month")
    int addViews(@Param("code") String code, @Param("month") String month, @Param("delta") long delta);

    List<PasteViewStats> findByShortCode(String shortCode);

    // @Query("SELECT s.month FROM PasteViewStats s WHERE s.shortCode = :shortCode")
    // Optional<List<String>> findMonthsByShortCode(@Param("shortCode") String
    // shortCode);
}
