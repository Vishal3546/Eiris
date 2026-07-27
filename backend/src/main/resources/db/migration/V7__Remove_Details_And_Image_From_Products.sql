-- Remove details and image_url columns from products table
ALTER TABLE products DROP COLUMN IF EXISTS details;
ALTER TABLE products DROP COLUMN IF EXISTS image_url;
