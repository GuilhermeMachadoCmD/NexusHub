CREATE TABLE cash_flow (
    id             BIGINT         NOT NULL AUTO_INCREMENT,
    type           ENUM('IN','OUT') NOT NULL,
    description    VARCHAR(255)   NOT NULL,
    amount         DECIMAL(12, 2) NOT NULL,
    reference_id   BIGINT,
    reference_type VARCHAR(50),
    occurred_at    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
