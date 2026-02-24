package com.nexus.finance.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface CashFlowRepository extends JpaRepository<CashFlow, Long> {

    Page<CashFlow> findAllByOrderByOccurredAtDesc(Pageable pageable);

    Page<CashFlow> findByTypeOrderByOccurredAtDesc(CashFlow.Type type, Pageable pageable);

    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM CashFlow c WHERE c.type = 'IN' AND c.occurredAt BETWEEN :start AND :end")
    BigDecimal sumInBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM CashFlow c WHERE c.type = 'OUT' AND c.occurredAt BETWEEN :start AND :end")
    BigDecimal sumOutBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
