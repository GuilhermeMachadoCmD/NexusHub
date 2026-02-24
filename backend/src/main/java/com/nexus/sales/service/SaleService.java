package com.nexus.sales.service;

import com.nexus.affiliates.domain.Affiliate;
import com.nexus.affiliates.domain.AffiliateRepository;
import com.nexus.affiliates.domain.Commission;
import com.nexus.affiliates.domain.CommissionRepository;
import com.nexus.campaigns.domain.Campaign;
import com.nexus.campaigns.domain.CampaignRepository;
import com.nexus.finance.domain.CashFlow;
import com.nexus.finance.domain.CashFlowRepository;
import com.nexus.sales.domain.*;
import com.nexus.shared.realtime.RealTimeService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final AffiliateRepository affiliateRepository;
    private final CampaignRepository campaignRepository;
    private final CommissionRepository commissionRepository;
    private final CashFlowRepository cashFlowRepository;
    private final RealTimeService realTimeService;

    @Transactional
    public Sale processWebhook(WebhookRequest req) {
        // Idempotency check
        if (saleRepository.findByPaymentGatewayId(req.getPaymentGatewayId()).isPresent()) {
            log.info("Duplicate webhook ignored: {}", req.getPaymentGatewayId());
            return saleRepository.findByPaymentGatewayId(req.getPaymentGatewayId()).get();
        }

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + req.getProductId()));

        Affiliate affiliate = req.getAffiliateId() != null
                ? affiliateRepository.findById(req.getAffiliateId()).orElse(null)
                : null;

        Campaign campaign = req.getCampaignId() != null
                ? campaignRepository.findById(req.getCampaignId()).orElse(null)
                : null;

        // 1. Save Sale
        Sale sale = Sale.builder()
                .paymentGatewayId(req.getPaymentGatewayId())
                .product(product)
                .affiliate(affiliate)
                .campaign(campaign)
                .amount(req.getAmount())
                .buyerName(req.getBuyerName())
                .buyerEmail(req.getBuyerEmail())
                .status(Sale.Status.PAID)
                .build();
        sale = saleRepository.save(sale);

        // 2. Auto cash flow IN
        CashFlow cashFlow = CashFlow.builder()
                .type(CashFlow.Type.IN)
                .description("Venda: " + product.getName() + " - " + req.getBuyerName())
                .amount(req.getAmount())
                .referenceId(sale.getId())
                .referenceType("SALE")
                .build();
        cashFlowRepository.save(cashFlow);

        // 3. Auto commission if affiliate present
        if (affiliate != null) {
            BigDecimal commissionAmount = req.getAmount()
                    .multiply(affiliate.getCommissionPct())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            Commission commission = Commission.builder()
                    .sale(sale)
                    .affiliate(affiliate)
                    .amount(commissionAmount)
                    .status(Commission.Status.PENDING)
                    .build();
            commissionRepository.save(commission);
        }

        // 4. Publish real-time event
        realTimeService.publishSaleEvent(new SaleEvent(
                sale.getId(), sale.getPaymentGatewayId(), sale.getAmount(),
                product.getName(), sale.getBuyerName()));
        realTimeService.publishDashboardUpdate("REFRESH");

        log.info("Sale processed: id={}, gateway={}, amount={}", sale.getId(), req.getPaymentGatewayId(), req.getAmount());
        return sale;
    }

    public Page<Sale> listSales(Pageable pageable) {
        return saleRepository.findAllByOrderBySoldAtDesc(pageable);
    }

    @Data
    public static class WebhookRequest {
        @NotBlank private String paymentGatewayId;
        @NotNull  private Long productId;
        private Long affiliateId;
        private Long campaignId;
        @NotNull  private BigDecimal amount;
        @NotBlank private String buyerName;
        private String buyerEmail;
    }

    public record SaleEvent(Long id, String gatewayId, BigDecimal amount, String product, String buyer) {}
}
