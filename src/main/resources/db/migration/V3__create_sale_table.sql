CREATE TABLE sale (
id UUID PRIMARY KEY,
sale_date DATE NOT NULL,
total_value FLOAT NOT NULL,
customer_id UUID NOT NULL,
FOREIGN KEY (customer_id) REFERENCES customer(id)
);