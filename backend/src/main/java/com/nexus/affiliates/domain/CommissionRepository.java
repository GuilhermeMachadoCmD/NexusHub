package com.nexus.affiliates.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, Long> {
    Optional<Commission> findBySaleId(Long saleId);
    List<Commission> findByAffiliateId(Long affiliateId);

    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM Commission c WHERE c.affiliate.id = :affiliateId AND c.status = 'PENDING'")
    java.math.BigDecimal sumPendingByAffiliate(@Param("affiliateId") Long affiliateId);
}
