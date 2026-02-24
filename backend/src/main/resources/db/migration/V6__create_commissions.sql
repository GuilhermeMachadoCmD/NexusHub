CREATE TABLE commissions (
    id           BIGINT         NOT NULL AUTO_INCREMENT,
    sale_id      BIGINT         NOT NULL,
    affiliate_id BIGINT         NOT NULL,
    amount       DECIMAL(12, 2) NOT NULL,
    status       ENUM('PENDING','PAID','CANCELLED') NOT NULL DEFAULT 'PENDING',
    paid_at      DATETIME,
    created_at   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_commissions_sale_id (sale_id),
    CONSTRAINT fk_commissions_sale      FOREIGN KEY (sale_id)      REFERENCES sales(id),
    CONSTRAINT fk_commissions_affiliate FOREIGN KEY (affiliate_id) REFERENCES affiliates(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
