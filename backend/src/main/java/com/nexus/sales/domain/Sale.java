package com.nexus.sales.domain;

import com.nexus.affiliates.domain.Affiliate;
import com.nexus.campaigns.domain.Campaign;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Sale {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_gateway_id", nullable = false, unique = true, length = 100)
    private String paymentGatewayId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliate_id")
    private Affiliate affiliate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PAID;

    @Column(name = "buyer_name", nullable = false, length = 150)
    private String buyerName;

    @Column(name = "buyer_email", length = 150)
    private String buyerEmail;

    @Column(name = "sold_at")
    private LocalDateTime soldAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist void prePersist() {
        createdAt = LocalDateTime.now();
        if (soldAt == null) soldAt = LocalDateTime.now();
    }

    public enum Status { PAID, REFUNDED, CHARGEBACK }
}
