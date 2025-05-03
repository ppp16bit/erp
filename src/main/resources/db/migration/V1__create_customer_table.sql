CREATE TABLE customer (
id UUID PRIMARY KEY,
name VARCHAR(255) NOT NULL,
email VARCHAR(255) NOT NULL,
phone VARCHAR(11) NOT NULL,
address VARCHAR(255),
customer_type VARCHAR(4) NOT NULL,
CONSTRAINT chk_customer_type CHECK (customer_type IN ('CPF', 'CNPJ'))
);