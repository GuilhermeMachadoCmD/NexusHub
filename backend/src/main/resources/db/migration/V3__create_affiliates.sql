CREATE TABLE affiliates (
    id             BIGINT         NOT NULL AUTO_INCREMENT,
    name           VARCHAR(150)   NOT NULL,
    email          VARCHAR(150)   NOT NULL,
    commission_pct DECIMAL(5, 2)  NOT NULL DEFAULT 10.00,
    status         ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_affiliates_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
