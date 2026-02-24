package com.nexus.sales.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    Optional<Sale> findByPaymentGatewayId(String paymentGatewayId);

    Page<Sale> findAllByOrderBySoldAtDesc(Pageable pageable);

    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM Sale s WHERE s.soldAt BETWEEN :start AND :end AND s.status = 'PAID'")
    BigDecimal sumRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(s) FROM Sale s WHERE s.soldAt BETWEEN :start AND :end AND s.status = 'PAID'")
    Long countSalesBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(AVG(s.amount), 0) FROM Sale s WHERE s.soldAt BETWEEN :start AND :end AND s.status = 'PAID'")
    BigDecimal avgTicketBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT s.campaign.id, SUM(s.amount) FROM Sale s WHERE s.campaign IS NOT NULL AND s.status = 'PAID' GROUP BY s.campaign.id")
    List<Object[]> revenueByCampaign();

    @Query("SELECT s.affiliate.id, SUM(s.amount), COUNT(s) FROM Sale s WHERE s.affiliate IS NOT NULL AND s.status = 'PAID' GROUP BY s.affiliate.id ORDER BY SUM(s.amount) DESC")
    List<Object[]> revenueByAffiliate();

    @Query("SELECT s.product.id, SUM(s.amount), COUNT(s) FROM Sale s WHERE s.status = 'PAID' GROUP BY s.product.id ORDER BY SUM(s.amount) DESC")
    List<Object[]> revenueByProduct();
}
