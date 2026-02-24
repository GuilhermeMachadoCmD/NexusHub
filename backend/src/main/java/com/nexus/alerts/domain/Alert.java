package com.nexus.alerts.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Alert {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false, length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity = Severity.WARNING;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @Column(name = "triggered_at")
    private LocalDateTime triggeredAt;

    @PrePersist void prePersist() { if (triggeredAt == null) triggeredAt = LocalDateTime.now(); }

    public enum Severity { INFO, WARNING, CRITICAL }
}
