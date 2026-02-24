CREATE TABLE sales (
    id                   BIGINT         NOT NULL AUTO_INCREMENT,
    payment_gateway_id   VARCHAR(100)   NOT NULL,
    product_id           BIGINT         NOT NULL,
    affiliate_id         BIGINT,
    campaign_id          BIGINT,
    amount               DECIMAL(12, 2) NOT NULL,
    status               ENUM('PAID','REFUNDED','CHARGEBACK') NOT NULL DEFAULT 'PAID',
    buyer_name           VARCHAR(150)   NOT NULL,
    buyer_email          VARCHAR(150),
    sold_at              DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at           DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_sales_payment_gateway_id (payment_gateway_id),
    CONSTRAINT fk_sales_product   FOREIGN KEY (product_id)   REFERENCES products(id),
    CONSTRAINT fk_sales_affiliate FOREIGN KEY (affiliate_id) REFERENCES affiliates(id),
    CONSTRAINT fk_sales_campaign  FOREIGN KEY (campaign_id)  REFERENCES campaigns(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
