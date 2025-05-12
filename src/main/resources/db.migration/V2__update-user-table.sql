ALTER TABLE users ADD COLUMN IF NOT EXISTS activated BOOLEAN;

UPDATE users SET activated = true;

CREATE TABLE activation_codes (
	id BIGSERIAL NOT NULL,
	code VARCHAR(100),
	email VARCHAR(200),
	created_at timestamp(6) without time zone,
    expires_at timestamp(6) without time zone,
    used boolean NOT NULL,
    used_at timestamp(6) without time zone
);