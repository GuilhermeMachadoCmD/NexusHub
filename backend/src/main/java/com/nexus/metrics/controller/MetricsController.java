package com.nexus.metrics.controller;

import com.nexus.sales.domain.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final SaleRepository saleRepository;

    @GetMapping("/dashboard")
    @Cacheable(value = "dashboard-metrics", key = "'main'")
    public ResponseEntity<Map<String, Object>> dashboard() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd   = LocalDate.now().atTime(23, 59, 59);
        LocalDateTime monthStart = YearMonth.now().atDay(1).atStartOfDay();
        LocalDateTime monthEnd   = YearMonth.now().atEndOfMonth().atTime(23, 59, 59);

        BigDecimal todayRevenue  = saleRepository.sumRevenueBetween(todayStart, todayEnd);
        BigDecimal monthRevenue  = saleRepository.sumRevenueBetween(monthStart, monthEnd);
        Long todaySales          = saleRepository.countSalesBetween(todayStart, todayEnd);
        Long monthSales          = saleRepository.countSalesBetween(monthStart, monthEnd);
        BigDecimal avgTicket     = saleRepository.avgTicketBetween(monthStart, monthEnd);

        return ResponseEntity.ok(Map.of(
                "todayRevenue",  todayRevenue,
                "monthRevenue",  monthRevenue,
                "todaySales",    todaySales,
                "monthSales",    monthSales,
                "avgTicket",     avgTicket,
                "period",        YearMonth.now().toString()
        ));
    }

    @GetMapping("/by-campaign")
    @Cacheable(value = "metrics-by-campaign", key = "'campaign'")
    public ResponseEntity<List<Map<String, Object>>> byCampaign() {
        List<Object[]> rows = saleRepository.revenueByCampaign();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(Map.of("campaignId", row[0], "revenue", row[1]));
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/by-affiliate")
    @Cacheable(value = "metrics-by-affiliate", key = "'affiliate'")
    public ResponseEntity<List<Map<String, Object>>> byAffiliate() {
        List<Object[]> rows = saleRepository.revenueByAffiliate();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(Map.of("affiliateId", row[0], "revenue", row[1], "salesCount", row[2]));
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/by-product")
    @Cacheable(value = "metrics-by-product", key = "'product'")
    public ResponseEntity<List<Map<String, Object>>> byProduct() {
        List<Object[]> rows = saleRepository.revenueByProduct();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(Map.of("productId", row[0], "revenue", row[1], "salesCount", row[2]));
        }
        return ResponseEntity.ok(result);
    }
}
