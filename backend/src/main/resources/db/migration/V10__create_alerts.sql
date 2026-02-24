CREATE TABLE alerts (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    type         VARCHAR(50)  NOT NULL,
    message      VARCHAR(500) NOT NULL,
    severity     ENUM('INFO','WARNING','CRITICAL') NOT NULL DEFAULT 'WARNING',
    is_read      BOOLEAN      NOT NULL DEFAULT FALSE,
    triggered_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_alerts_is_read (is_read),
    INDEX idx_alerts_triggered_at (triggered_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
