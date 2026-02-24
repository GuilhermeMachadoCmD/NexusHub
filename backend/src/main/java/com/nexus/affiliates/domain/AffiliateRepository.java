package com.nexus.affiliates.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AffiliateRepository extends JpaRepository<Affiliate, Long> {
    List<Affiliate> findByStatus(Affiliate.Status status);
}
