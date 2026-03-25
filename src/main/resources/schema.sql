CREATE TABLE IF NOT EXISTS customers (
  id BIGINT PRIMARY KEY,
  name VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS transactions (
  id BIGINT PRIMARY KEY,
  customer_id BIGINT,
  amount DOUBLE,
  transaction_date DATE,
  FOREIGN KEY (customer_id) REFERENCES customers(id)
);