package com.nexus.campaigns.controller;

import com.nexus.campaigns.domain.Campaign;
import com.nexus.campaigns.domain.CampaignRepository;
import com.nexus.sales.domain.SaleRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignRepository campaignRepository;
    private final SaleRepository saleRepository;

    @GetMapping
    public ResponseEntity<List<Campaign>> list() {
        return ResponseEntity.ok(campaignRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Campaign> get(@PathVariable Long id) {
        return campaignRepository.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Campaign> create(@Valid @RequestBody CampaignRequest req) {
        Campaign c = Campaign.builder()
                .name(req.getName())
                .channel(req.getChannel())
                .investment(req.getInvestment())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .active(true)
                .build();
        return ResponseEntity.ok(campaignRepository.save(c));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Campaign> update(@PathVariable Long id, @Valid @RequestBody CampaignRequest req) {
        return campaignRepository.findById(id).map(c -> {
            c.setName(req.getName());
            c.setChannel(req.getChannel());
            c.setInvestment(req.getInvestment());
            c.setStartDate(req.getStartDate());
            c.setEndDate(req.getEndDate());
            return ResponseEntity.ok(campaignRepository.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/roi")
    public ResponseEntity<Map<String, Object>> roi(@PathVariable Long id) {
        return campaignRepository.findById(id).map(c -> {
            LocalDateTime start = c.getStartDate() != null ? c.getStartDate().atStartOfDay() : LocalDateTime.now().minusYears(1);
            LocalDateTime end   = c.getEndDate()   != null ? c.getEndDate().atTime(23, 59, 59) : LocalDateTime.now();
            BigDecimal revenue = saleRepository.sumRevenueBetween(start, end);
            BigDecimal roi = BigDecimal.ZERO;
            if (c.getInvestment().compareTo(BigDecimal.ZERO) > 0) {
                roi = revenue.subtract(c.getInvestment())
                        .divide(c.getInvestment(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
            }
            return ResponseEntity.ok(Map.of(
                    "campaignId", id,
                    "investment", c.getInvestment(),
                    "revenue", revenue,
                    "roi", roi
            ));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Data
    public static class CampaignRequest {
        @NotBlank private String name;
        private String channel;
        @NotNull private BigDecimal investment;
        private LocalDate startDate;
        private LocalDate endDate;
    }
}
