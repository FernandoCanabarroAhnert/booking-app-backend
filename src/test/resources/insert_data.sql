INSERT INTO roles (authority) VALUES ('ROLE_GUEST');
INSERT INTO roles (authority) VALUES ('ROLE_OPERATOR');
INSERT INTO roles (authority) VALUES ('ROLE_ADMIN');

INSERT INTO hotels (name, description, room_quantity, street, number, city, zip_code, state, phone)
VALUES 
  ('Hotel Mar Azul', 'Hotel com vista para o mar, ideal para férias e descanso.', 45, 'Av. Beira Mar', '123', 'Fortaleza', '60165-121', 'CE', '(85) 99999-1234');
INSERT INTO hotels (name, description, room_quantity, street, number, city, zip_code, state, phone)
VALUES 
  ('Hotel Serra Verde', 'Localizado nas montanhas, perfeito para quem busca tranquilidade e natureza.', 30, 'Rua das Palmeiras', '456', 'Campos do Jordão', '12460-000', 'SP', '(12) 98888-5678');
INSERT INTO hotels (name, description, room_quantity, street, number, city, zip_code, state, phone)
VALUES 
  ('Hotel Central Plaza', 'Hotel no centro da cidade com fácil acesso a comércio e transporte.', 60, 'Rua XV de Novembro', '789', 'Curitiba', '80020-310', 'PR', '(41) 97777-9101');

INSERT INTO images (base64image, image_type, hotel_id) VALUES ('', 'HOTEL', 1);
INSERT INTO images (base64image, image_type, hotel_id) VALUES ('', 'HOTEL', 2);
INSERT INTO images (base64image, image_type, hotel_id) VALUES ('', 'HOTEL', 3);

INSERT INTO users (full_name, email, password, phone, cpf, birth_date, created_at, activated, hotel_id) 
    VALUES ('Fernando', 'fernando@gmail.com', '$2a$10$vB8CKU3B8Arygzyb/nv/0Ol8YzL/YEATkB/O3pF9ltf1/B9hfhwc6', '(51) 1234-12345', '329.949.250-01', '2005-10-28', '2025-04-25 10:15:03.181656', true, 1);
INSERT INTO users (full_name, email, password, phone, cpf, birth_date, created_at, activated) 
    VALUES ('Anita', 'anita@gmail.com', '$2a$10$vB8CKU3B8Arygzyb/nv/0Ol8YzL/YEATkB/O3pF9ltf1/B9hfhwc6', '(51) 1234-12345', '123.456.789-10', '2005-10-28', '2025-04-25 10:15:03.181656', true);
INSERT INTO users (full_name, email, password, phone, cpf, birth_date, created_at, activated) 
    VALUES ('William', 'william@gmail.com', '$2a$10$vB8CKU3B8Arygzyb/nv/0Ol8YzL/YEATkB/O3pF9ltf1/B9hfhwc6', '(51) 1234-12345', '062.083.260-60', '2005-06-28', '2025-04-25 10:15:03.181656', false);
INSERT INTO users (full_name, email, password, phone, cpf, birth_date, created_at, activated) 
    VALUES ('Pereira', 'pereira@gmail.com', '$2a$10$vB8CKU3B8Arygzyb/nv/0Ol8YzL/YEATkB/O3pF9ltf1/B9hfhwc6', '(51) 1234-12345', '786.857.060-17', '2005-06-28', '2025-04-25 10:15:03.181656', true);

INSERT INTO user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO user_role (user_id, role_id) VALUES (1, 2);
INSERT INTO user_role (user_id, role_id) VALUES (1, 3);
INSERT INTO user_role (user_id, role_id) VALUES (2, 1);
INSERT INTO user_role (user_id, role_id) VALUES (3, 1);
INSERT INTO user_role (user_id, role_id) VALUES (4, 1);

INSERT INTO rooms (number, floor, type, price_per_night, description, capacity, hotel_id)
  VALUES ('101', 1, 'SINGLE', 150.00, 'Quarto standard com cama de casal e ar-condicionado.', 2, 1);
INSERT INTO rooms (number, floor, type, price_per_night, description, capacity, hotel_id)
  VALUES ('202', 2, 'DOUBLE', 250.00, 'Quarto deluxe espaçoso com vista para o mar e varanda.', 3, 2);
INSERT INTO rooms (number, floor, type, price_per_night, description, capacity, hotel_id)
  VALUES ('303', 2, 'SUITE', 350.00, 'Quarto deluxe espaçoso com vista para o mar e varanda.', 3, 2);

INSERT INTO images (base64image, image_type, room_id) VALUES ('', 'ROOM', 1);
INSERT INTO images (base64image, image_type, room_id) VALUES ('', 'ROOM', 2);
INSERT INTO images (base64image, image_type, room_id) VALUES ('', 'ROOM', 3);

INSERT INTO room_ratings (room_id, user_id, rating, description, created_at)
  VALUES (1, 1, 4.5, 'Quarto muito bom e confortável.', '2025-04-25 10:15:03.181656');
INSERT INTO room_ratings (room_id, user_id, rating, description, created_at)
  VALUES (2, 2, 4.5, 'Quarto muito bom e confortável.', '2025-04-25 10:15:03.181656');

INSERT INTO payments (amount, is_online_payment, payment_type) VALUES (750.00, false, 'DINHEIRO');

INSERT INTO dinheiro_payments (id) VALUES (1);

INSERT INTO bookings (check_in, check_out, created_at, is_finished, payment_id, room_id, user_id, guests_quantity) 
  VALUES ('2025-05-02', '2025-05-07', '2025-05-02 09:20:10.886597', true, 1, 1, 2, 1);

INSERT INTO credit_cards (brand, card_number, cvv, expiration_date, holder_name, user_id) VALUES ('VISA', '1234567812345678', '123', '2026-08-01', 'Fernando', 1);

INSERT INTO activation_codes (code, email, created_at, expires_at, used, used_at)
  VALUES ('123456', 'william@gmail.com', NOW(), NOW() + INTERVAL '30 MINUTES', false, NULL);
INSERT INTO activation_codes (code, email, created_at, expires_at, used, used_at)
  VALUES ('654321', 'william@gmail.com', NOW() - INTERVAL '35 MINUTES', NOW() - INTERVAL '5 MINUTES', false, NULL);

INSERT INTO password_recovers (code, created_at, expires_at, used, used_at, user_id)
  VALUES ('123456', NOW(), NOW() + INTERVAL '30 MINUTES', false, NULL, 3);
INSERT INTO password_recovers (code, created_at, expires_at, used, used_at, user_id)
  VALUES ('654321', NOW() - INTERVAL '35 MINUTES', NOW() - INTERVAL '5 MINUTES', false, NULL, 3);
  