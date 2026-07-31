-- Remove price and stock columns from index_products table
ALTER TABLE index_products DROP COLUMN IF EXISTS price;
ALTER TABLE index_products DROP COLUMN IF EXISTS stock;
