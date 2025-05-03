CREATE TABLE sale_item(
id UUID PRIMARY KEY,
quantity INT NOT NULL,
unit_price FLOAT NOT NULL,
sale_id UUID NOT NULL,
product_id UUID NOT NULL,
FOREIGN KEY (sale_id) REFERENCES sale(id),
FOREIGN KEY (product_id) REFERENCES product(id)
);