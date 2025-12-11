-- Insert roles
INSERT IGNORE INTO roles (nombre, estado) VALUES ('SECRETARIA', 'A');
INSERT IGNORE INTO roles (nombre, estado) VALUES ('COORDINADORA', 'A');
INSERT IGNORE INTO roles (nombre, estado) VALUES ('DOCTOR', 'A');
INSERT IGNORE INTO roles (nombre, estado) VALUES ('PASANTES', 'A');

-- Insert users (contraseñas encriptadas con BCrypt)
INSERT IGNORE INTO usuarios (cedula, contrasenia, estado, email, nombres, apellidos, fecha_creacion, fecha_modificacion) VALUES
('1234567890', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'A', 'secretaria@example.com', 'Ana', 'Perez', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT IGNORE INTO usuarios (cedula, contrasenia, estado, email, nombres, apellidos, fecha_creacion, fecha_modificacion) VALUES
('2345678901', '$2a$10$yE9RpT5pUJe7/VKPQmDdXeKEuT5xN5tHQqXLzKJQYKNZJ5kzJZH6O', 'A', 'coordinadora@example.com', 'Beatriz', 'Lopez', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT IGNORE INTO usuarios (cedula, contrasenia, estado, email, nombres, apellidos, fecha_creacion, fecha_modificacion) VALUES
('3456789012', '$2a$10$UqzVmKZLQXJDQQNLMDYlLeEQxqKxZ7GzQX2xq2JL5uLXqXr5sKzKO', 'A', 'doctor@example.com', 'Carlos', 'Garcia', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT IGNORE INTO usuarios (cedula, contrasenia, estado, email, nombres, apellidos, fecha_creacion, fecha_modificacion) VALUES
('4567890123', '$2a$10$LwKq5X2VqJWVqJWVqJWVqeHqXqXqXqXqXqXqXqXqXqXqXqXqXqXqXq', 'A', 'pasante@example.com', 'David', 'Martinez', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Asignar roles
INSERT IGNORE INTO usuarios_roles (id_usuario, id_rol, fecha_asignacion, estado) VALUES (1, 1, CURRENT_TIMESTAMP, 'A');
INSERT IGNORE INTO usuarios_roles (id_usuario, id_rol, fecha_asignacion, estado) VALUES (2, 2, CURRENT_TIMESTAMP, 'A');
INSERT IGNORE INTO usuarios_roles (id_usuario, id_rol, fecha_asignacion, estado) VALUES (3, 3, CURRENT_TIMESTAMP, 'A');
INSERT IGNORE INTO usuarios_roles (id_usuario, id_rol, fecha_asignacion, estado) VALUES (4, 4, CURRENT_TIMESTAMP, 'A');INSERT IGNORE INTO areas (nombre, estado) VALUES ('Psicología', 'A'), ('Psicología Educativa', 'A'), ('Psicología Clínica', 'A'), ('Fonoaudiología', 'A'), ('Estimulación Temprana', 'A'), ('Terapia Física', 'A'), ('Trabajo Social', 'A'), ('Odontología', 'A');
