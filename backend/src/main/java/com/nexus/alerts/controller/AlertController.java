package com.nexus.alerts.controller;

import com.nexus.alerts.domain.Alert;
import com.nexus.alerts.domain.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertRepository alertRepository;

    @GetMapping
    public ResponseEntity<Page<Alert>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(alertRepository.findAllByOrderByTriggeredAtDesc(PageRequest.of(page, size)));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCount() {
        return ResponseEntity.ok(Map.of("count", alertRepository.countByReadFalse()));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Alert> markRead(@PathVariable Long id) {
        return alertRepository.findById(id).map(a -> {
            a.setRead(true);
            return ResponseEntity.ok(alertRepository.save(a));
        }).orElse(ResponseEntity.notFound().build());
    }
}
