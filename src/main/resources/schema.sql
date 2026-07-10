-- Schema initialization for H2 Database
-- This script is executed automatically by Spring Boot on startup

CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id VARCHAR(50) NOT NULL UNIQUE,
    customer_id VARCHAR(50) NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    transaction_amount DECIMAL(19,4) NOT NULL,
    transaction_date DATE NOT NULL
    );

CREATE INDEX IF NOT EXISTS idx_customer_id ON transactions (customer_id);

CREATE INDEX IF NOT EXISTS idx_transaction_date ON transactions (transaction_date);