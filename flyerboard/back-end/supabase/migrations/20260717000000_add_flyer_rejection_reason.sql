-- Add the rejection_reason column so a moderator's reason for rejecting a flyer is actually
-- persisted. The API and FlyerNetworkResponse already documented this field; the column to
-- back it was never created.

ALTER TABLE flyers
    ADD COLUMN rejection_reason TEXT;
