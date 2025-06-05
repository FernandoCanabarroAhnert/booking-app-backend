ALTER TABLE bookings DROP CONSTRAINT IF EXISTS fk_booking_payment;

ALTER TABLE bookings
ADD CONSTRAINT fk_booking_payment
FOREIGN KEY (payment_id)
REFERENCES payments(id)
ON DELETE CASCADE;

ALTER TABLE boleto_payments DROP CONSTRAINT IF EXISTS fk337rel9sbese11af8u87nn5hv;

ALTER TABLE boleto_payments
ADD CONSTRAINT fk_payment_id
FOREIGN KEY (id)
REFERENCES payments(id)
ON DELETE CASCADE;

ALTER TABLE dinheiro_payments DROP CONSTRAINT IF EXISTS fkmi76d9acw0scxyxdodhn2wanb;

ALTER TABLE dinheiro_payments
ADD CONSTRAINT fk_payment_id
FOREIGN KEY (id)
REFERENCES payments(id)
ON DELETE CASCADE;

ALTER TABLE pix_payments DROP CONSTRAINT IF EXISTS fk9krs9wtbw614oqrkwgu939xe2;

ALTER TABLE pix_payments
ADD CONSTRAINT fk_payment_id
FOREIGN KEY (id)
REFERENCES payments(id)
ON DELETE CASCADE;

ALTER TABLE cartao_payments DROP CONSTRAINT IF EXISTS fkgg3559g2sm981a95w47gxux9;

ALTER TABLE cartao_payments
ADD CONSTRAINT fk_payment_id
FOREIGN KEY (id)
REFERENCES payments(id)
ON DELETE CASCADE;