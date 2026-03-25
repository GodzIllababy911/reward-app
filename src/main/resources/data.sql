INSERT INTO customers (id, name)
VALUES (1, 'John'), (2, 'Alice'), (3, 'Ken')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO transactions (id, customer_id, amount, transaction_date)
VALUES
-- Customer 1 (John)
(1, 1, 120, '2026-01-10'),
(2, 1, 80,  '2026-02-15'),
(3, 1, 200, '2026-03-20'),
(6, 1, 75,  '2026-04-12'),
(7, 1, 130, '2026-05-18'),

-- Customer 2 (Alice)
(4, 2, 90,  '2026-01-05'),
(5, 2, 150, '2026-02-10'),
(8, 2, 60,  '2026-03-08'),
(9, 2, 110, '2026-04-22'),
(10,2, 170, '2026-05-25')

ON DUPLICATE KEY UPDATE
amount = VALUES(amount),
transaction_date = VALUES(transaction_date);