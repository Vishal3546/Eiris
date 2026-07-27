-- Replace client_state with client_gst_no in agency-clients table
ALTER TABLE "agency-clients" DROP COLUMN IF EXISTS client_state;
ALTER TABLE "agency-clients" ADD COLUMN IF NOT EXISTS client_gst_no VARCHAR(50);
