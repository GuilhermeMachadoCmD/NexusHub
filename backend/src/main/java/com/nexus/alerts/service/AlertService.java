package com.nexus.alerts.service;

import com.nexus.alerts.domain.Alert;
import com.nexus.alerts.domain.AlertRepository;
import com.nexus.sales.domain.SaleRepository;
import com.nexus.shared.realtime.RealTimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final SaleRepository saleRepository;
    private final AlertRepository alertRepository;
    private final RealTimeService realTimeService;

    @Scheduled(fixedDelay = 1800000) // 30 minutes
    public void runAlerts() {
        log.info("Running scheduled alert analysis...");
        checkConversionDrop();
    }

    private void checkConversionDrop() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime last24h = now.minusHours(24);
        LocalDateTime last7d  = now.minusDays(7);
        LocalDateTime last8d  = now.minusDays(8);

        long sales24h = saleRepository.countSalesBetween(last24h, now);
        long salesPrev7d = saleRepository.countSalesBetween(last7d, last24h);

        if (salesPrev7d > 0) {
            double avgPer24h = (double) salesPrev7d / 7;
            double dropPct = (avgPer24h - sales24h) / avgPer24h * 100;

            if (dropPct > 20) {
                String message = String.format(
                        "Queda de %.1f%% nas conversões nas últimas 24h (média 7d: %.1f, atual: %d)",
                        dropPct, avgPer24h, sales24h);
                saveAndPublish("CONVERSION_DROP", message, Alert.Severity.WARNING);
            }
        }
    }

    private void saveAndPublish(String type, String message, Alert.Severity severity) {
        Alert alert = Alert.builder()
                .type(type)
                .message(message)
                .severity(severity)
                .read(false)
                .build();
        alert = alertRepository.save(alert);
        realTimeService.publishAlertEvent(new AlertEvent(alert.getId(), alert.getType(),
                alert.getMessage(), alert.getSeverity().name(), alert.getTriggeredAt()));
        log.warn("Alert triggered: [{}] {}", type, message);
    }

    public record AlertEvent(Long id, String type, String message, String severity, LocalDateTime triggeredAt) {}
}
