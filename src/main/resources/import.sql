INSERT INTO roles (authority) VALUES ('ROLE_GUEST');
INSERT INTO roles (authority) VALUES ('ROLE_OPERATOR');
INSERT INTO roles (authority) VALUES ('ROLE_ADMIN');

INSERT INTO hotels (name, room_quantity, address, city, zip_code, state, phone) VALUES ('Hotel Paradise', 10, '123 Ocean Drive', 'Porto Alegre', '94450530', 'RS', '51995214017');

INSERT INTO rooms (number, floor, type, price_per_night, description, capacity, hotel_id) VALUES ('204', 2, 1, 350.00, 'Suíte com vista para o mar e varanda privativa', 4, 1);

INSERT INTO users (full_name, email, password, phone, cpf, birth_date, created_at, hotel_id) VALUES ('Fernando', 'fernando@gmail.com', '$2a$10$vB8CKU3B8Arygzyb/nv/0Ol8YzL/YEATkB/O3pF9ltf1/B9hfhwc6', '(51) 99521-4017', '873.582.800-59', '2005-10-28', '2025-04-25 10:15:03.181656', 1);
INSERT INTO users (full_name, email, password, phone, cpf, birth_date, created_at) VALUES ('Anita', 'anita@gmail.com', '$2a$10$vB8CKU3B8Arygzyb/nv/0Ol8YzL/YEATkB/O3pF9ltf1/B9hfhwc6', '(51) 99521-4017', '123.456.789-10', '2005-10-28', '2025-04-25 10:15:03.181656');

INSERT INTO user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO user_role (user_id, role_id) VALUES (1, 2);
INSERT INTO user_role (user_id, role_id) VALUES (1, 3);
INSERT INTO user_role (user_id, role_id) VALUES (2, 1);