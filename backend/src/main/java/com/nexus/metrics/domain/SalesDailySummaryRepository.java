package com.nexus.metrics.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.time.LocalDate;

@Repository
public interface SalesDailySummaryRepository extends JpaRepository<SalesDailySummary, Long> {
    Optional<SalesDailySummary> findBySummaryDate(LocalDate date);
}
