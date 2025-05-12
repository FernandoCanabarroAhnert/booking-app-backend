ALTER TABLE bookings ADD COLUMN IF NOT EXISTS guests_quantity INT;

UPDATE bookings SET guests_quantity = 1 WHERE guests_quantity IS NULL;