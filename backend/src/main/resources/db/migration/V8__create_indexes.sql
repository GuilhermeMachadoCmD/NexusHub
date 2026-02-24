-- Performance indexes for sales queries
CREATE INDEX idx_sales_sold_at         ON sales (sold_at);
CREATE INDEX idx_sales_status          ON sales (status);
CREATE INDEX idx_sales_campaign_date   ON sales (campaign_id, sold_at);
CREATE INDEX idx_sales_affiliate_date  ON sales (affiliate_id, sold_at);
CREATE INDEX idx_sales_product_date    ON sales (product_id, sold_at);

-- Performance indexes for cash_flow
CREATE INDEX idx_cash_flow_type        ON cash_flow (type);
CREATE INDEX idx_cash_flow_occurred_at ON cash_flow (occurred_at);
CREATE INDEX idx_cash_flow_type_date   ON cash_flow (type, occurred_at);

-- Performance indexes for commissions
CREATE INDEX idx_commissions_affiliate ON commissions (affiliate_id);
CREATE INDEX idx_commissions_status    ON commissions (status);
