--liquibase formatted sql

--changeset changelog-init:1
CREATE TABLE IF NOT EXISTS product (
    product_id uuid NOT NULL,
    product_code character varying(50) NOT NULL,
    description character varying(255) NOT NULL,
    product_type character varying(50) NOT NULL,
    supplier_value double precision NOT NULL,
    available_amount numeric NOT NULL,
    CONSTRAINT pk_product PRIMARY KEY (product_id),
    UNIQUE(product_code)
);

--changeset changelog-init:2
CREATE TABLE IF NOT EXISTS inventory (
    inventory_id uuid NOT NULL,
    product_id uuid NOT NULL,
    inventory_type character varying(50) NOT NULL,
    sale_value double precision,
    sale_date timestamp(6) without time zone,
    inventory_amount numeric NOT NULL,
    CONSTRAINT pk_inventory PRIMARY KEY (inventory_id),
    CONSTRAINT fk_product FOREIGN KEY(product_id) REFERENCES product(product_id)
);
