CREATE TABLE IF NOT EXISTS hotels (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    room_quantity INTEGER,
    street VARCHAR(255),
    number VARCHAR(255),
    city VARCHAR(255),
    zip_code VARCHAR(20),
    state VARCHAR(100),
    phone VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS rooms (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    capacity integer,
    description character varying(255),
    floor integer,
    number character varying(255),
    price_per_night numeric(38,2),
    type character varying(255),
    hotel_id bigint,
    CONSTRAINT fk_rooms_hotel FOREIGN KEY (hotel_id) REFERENCES hotels(id)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255),
    phone VARCHAR(255),
    cpf VARCHAR(255),
    birth_date DATE,
    created_at TIMESTAMP,
    activated BOOLEAN,
    hotel_id BIGINT,
    CONSTRAINT fk_user_hotel FOREIGN KEY (hotel_id) REFERENCES hotels(id)
);

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL,
    authority VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS user_role (
    role_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, user_id),
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES roles(id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS images (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    base64image text,
    image_type character varying(255),
    hotel_id bigint,
    room_id bigint,
    CONSTRAINT fk_image_hotel FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE,
    CONSTRAINT fk_image_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS password_recovers (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    code character varying(255),
    created_at timestamp(6) without time zone,
    expires_at timestamp(6) without time zone,
    used boolean NOT NULL,
    used_at timestamp(6) without time zone,
    user_id bigint,
    CONSTRAINT fk_password_recover_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS room_ratings (
    id BIGSERIAL PRIMARY KEY,
    rating numeric(2, 1),
    description text,
    created_at timestamp(6) without time zone,
    room_id BIGINT,
    user_id BIGINT,
    CONSTRAINT fk_room_rating_room_id FOREIGN KEY (room_id) REFERENCES rooms (id),
    CONSTRAINT fk_room_rating_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS credit_cards (
    id BIGSERIAL PRIMARY KEY,
    brand character varying(255),
    card_number character varying(255),
    cvv character varying(255),
    expiration_date date,
    holder_name character varying(255),
    user_id bigint,
    CONSTRAINT fk_credit_card_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS activation_codes (
	id BIGSERIAL PRIMARY KEY,
	code VARCHAR(100),
	email VARCHAR(200),
	created_at timestamp(6) without time zone,
    expires_at timestamp(6) without time zone,
    used boolean NOT NULL,
    used_at timestamp(6) without time zone
);

CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    amount numeric(38,2),
    is_online_payment boolean NOT NULL,
    payment_type character varying(255)
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGSERIAL PRIMARY KEY,
    check_in date,
    check_out date,
    guests_quantity INT,
    created_at timestamp(6) without time zone,
    is_finished boolean NOT NULL,
    payment_id bigint,
    room_id bigint,
    user_id bigint,
    CONSTRAINT fk_booking_room_id FOREIGN KEY (room_id) REFERENCES rooms (id),
    CONSTRAINT fk_booking_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_booking_payment_id FOREIGN KEY (payment_id) REFERENCES payments (id)
);

CREATE TABLE IF NOT EXISTS boleto_payments (
    expiration_date date,
    id BIGSERIAL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS cartao_payments (
    brand character varying(255),
    card_holder_name character varying(255),
    credit_card_id bigint,
    expiration_date date,
    installment_quantity integer,
    last_four_digits character varying(255),
    id BIGSERIAL PRIMARY KEY
);

CREATE TABLE public.dinheiro_payments (
    id BIGSERIAL PRIMARY KEY
);

CREATE TABLE public.pix_payments (
    id BIGSERIAL PRIMARY KEY
);

ALTER TABLE ONLY cartao_payments
    ADD CONSTRAINT fkgg3559g2sm981a95w47gxux9 FOREIGN KEY (id) REFERENCES payments(id);

ALTER TABLE ONLY dinheiro_payments
    ADD CONSTRAINT fkmi76d9acw0scxyxdodhn2wanb FOREIGN KEY (id) REFERENCES payments(id);

ALTER TABLE ONLY public.boleto_payments
    ADD CONSTRAINT fk337rel9sbese11af8u87nn5hv FOREIGN KEY (id) REFERENCES public.payments(id);

ALTER TABLE ONLY public.pix_payments
    ADD CONSTRAINT fk9krs9wtbw614oqrkwgu939xe2 FOREIGN KEY (id) REFERENCES public.payments(id);

