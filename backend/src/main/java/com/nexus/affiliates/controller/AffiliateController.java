package com.nexus.affiliates.controller;

import com.nexus.affiliates.domain.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/affiliates")
@RequiredArgsConstructor
public class AffiliateController {

    private final AffiliateRepository affiliateRepository;
    private final CommissionRepository commissionRepository;

    @GetMapping
    public ResponseEntity<List<Affiliate>> list() {
        return ResponseEntity.ok(affiliateRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Affiliate> get(@PathVariable Long id) {
        return affiliateRepository.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Affiliate> create(@Valid @RequestBody AffiliateRequest req) {
        Affiliate a = Affiliate.builder()
                .name(req.getName())
                .email(req.getEmail())
                .commissionPct(req.getCommissionPct())
                .status(Affiliate.Status.ACTIVE)
                .build();
        return ResponseEntity.ok(affiliateRepository.save(a));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Affiliate> update(@PathVariable Long id, @Valid @RequestBody AffiliateRequest req) {
        return affiliateRepository.findById(id).map(a -> {
            a.setName(req.getName());
            a.setEmail(req.getEmail());
            a.setCommissionPct(req.getCommissionPct());
            return ResponseEntity.ok(affiliateRepository.save(a));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/commissions")
    public ResponseEntity<List<Commission>> commissions(@PathVariable Long id) {
        return ResponseEntity.ok(commissionRepository.findByAffiliateId(id));
    }

    @Data
    public static class AffiliateRequest {
        @NotBlank private String name;
        @Email @NotBlank private String email;
        @NotNull @DecimalMin("0.00") @DecimalMax("100.00")
        private BigDecimal commissionPct;
    }
}
