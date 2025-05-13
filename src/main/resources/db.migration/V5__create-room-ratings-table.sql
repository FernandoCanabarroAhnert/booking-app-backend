ALTER TABLE activation_codes ADD CONSTRAINT activation_codes_pkey PRIMARY KEY (id);

CREATE TABLE room_ratings (
    id BIGSERIAL PRIMARY KEY,
    rating numeric(2, 1),
    description text,
    created_at timestamp(6) without time zone,
    room_id BIGINT,
    user_id BIGINT,
    CONSTRAINT fk_room_rating_room_id FOREIGN KEY (room_id) REFERENCES rooms (id),
    CONSTRAINT fk_room_rating_user_id FOREIGN KEY (user_id) REFERENCES users (id)
)