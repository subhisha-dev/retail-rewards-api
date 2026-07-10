-- Sample data initialization for H2 Database
-- This script populates the transaction table with sample data

INSERT INTO transactions (transaction_id, customer_id, customer_name, transaction_amount, transaction_date) VALUES
    ('T1', '1', 'John', 120.87, '2026-04-10'),
    ('T2', '1', 'John', 90.0, '2026-04-15'),
    ('T3', '1', 'John', 75.0, '2026-05-20'),
    ('T4', '1', 'John', 200.0, '2026-06-10'),
    ('T5', '1', 'John', 390.05, '2026-02-10'),
    ('T6', '2', 'Alex', 50.16, '2026-05-05'),
    ('T7', '2', 'Alex', 200.0, '2026-06-25'),
    ('T8', '3', 'Alice', 150.0, '2026-05-18'),
    ('T9', '3', 'Alice', 900.0, '2026-06-26'),
    ('T10', '4', 'Peter', 300.0, '2026-05-27'),
    ('T11', '4', 'Peter', 1000.0, '2026-06-12');