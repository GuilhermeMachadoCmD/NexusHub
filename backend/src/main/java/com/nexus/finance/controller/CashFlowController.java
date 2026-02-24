package com.nexus.finance.controller;

import com.nexus.finance.domain.CashFlow;
import com.nexus.finance.domain.CashFlowRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Map;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class CashFlowController {

    private final CashFlowRepository cashFlowRepository;

    @GetMapping("/cash-flow")
    public ResponseEntity<Page<CashFlow>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type) {
        if (type != null) {
            CashFlow.Type t = CashFlow.Type.valueOf(type.toUpperCase());
            return ResponseEntity.ok(cashFlowRepository.findByTypeOrderByOccurredAtDesc(t, PageRequest.of(page, size)));
        }
        return ResponseEntity.ok(cashFlowRepository.findAllByOrderByOccurredAtDesc(PageRequest.of(page, size)));
    }

    @PostMapping("/cash-flow")
    public ResponseEntity<CashFlow> createExpense(@Valid @RequestBody ExpenseRequest req) {
        CashFlow cf = CashFlow.builder()
                .type(CashFlow.Type.OUT)
                .description(req.getDescription())
                .amount(req.getAmount())
                .build();
        return ResponseEntity.ok(cashFlowRepository.save(cf));
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> summary(
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "0") int month) {
        YearMonth ym = (year == 0 || month == 0) ? YearMonth.now() : YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end   = ym.atEndOfMonth().atTime(23, 59, 59);
        BigDecimal totalIn  = cashFlowRepository.sumInBetween(start, end);
        BigDecimal totalOut = cashFlowRepository.sumOutBetween(start, end);
        return ResponseEntity.ok(Map.of(
                "period", ym.toString(),
                "totalIn", totalIn,
                "totalOut", totalOut,
                "balance", totalIn.subtract(totalOut)
        ));
    }

    @Data
    public static class ExpenseRequest {
        @NotBlank private String description;
        @NotNull private BigDecimal amount;
    }
}
