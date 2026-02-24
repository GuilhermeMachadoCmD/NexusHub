package com.nexus.sales.controller;

import com.nexus.sales.domain.Sale;
import com.nexus.sales.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping("/webhook")
    public ResponseEntity<Sale> webhook(@Valid @RequestBody SaleService.WebhookRequest req) {
        return ResponseEntity.ok(saleService.processWebhook(req));
    }

    @GetMapping
    public ResponseEntity<Page<Sale>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(saleService.listSales(pageable));
    }
}
