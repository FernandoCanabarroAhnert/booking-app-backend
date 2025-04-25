INSERT INTO roles (authority) VALUES ('ROLE_GUEST');
INSERT INTO roles (authority) VALUES ('ROLE_OPERATOR');
INSERT INTO roles (authority) VALUES ('ROLE_ADMIN');

INSERT INTO users (full_name, email, password, phone, cpf, birth_date, created_at) VALUES ('Fernando', 'fernando@gmail.com', '$2a$10$vB8CKU3B8Arygzyb/nv/0Ol8YzL/YEATkB/O3pF9ltf1/B9hfhwc6', '(51) 99521-4017', '873.582.800-59', '2005-10-28', '2025-04-25 10:15:03.181656');

INSERT INTO user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO user_role (user_id, role_id) VALUES (1, 2);
INSERT INTO user_role (user_id, role_id) VALUES (1, 3);