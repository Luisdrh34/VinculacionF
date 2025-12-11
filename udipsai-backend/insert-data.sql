-- Insertar Áreas
INSERT INTO areas (nombre, estado) VALUES ('SECRETARÍA', 'A') ON CONFLICT (nombre) DO NOTHING;
INSERT INTO areas (nombre, estado) VALUES ('COORDINACIÓN', 'A') ON CONFLICT (nombre) DO NOTHING;
INSERT INTO areas (nombre, estado) VALUES ('PSICOLOGÍA EDUCATIVA', 'A') ON CONFLICT (nombre) DO NOTHING;
INSERT INTO areas (nombre, estado) VALUES ('ODONTOLOGÍA', 'A') ON CONFLICT (nombre) DO NOTHING;
INSERT INTO areas (nombre, estado) VALUES ('FONOAUDIOLOGÍA', 'A') ON CONFLICT (nombre) DO NOTHING;
INSERT INTO areas (nombre, estado) VALUES ('PSICOLOGÍA CLÍNICA', 'A') ON CONFLICT (nombre) DO NOTHING;
INSERT INTO areas (nombre, estado) VALUES ('ESTIMULACIÓN TEMPRANA', 'A') ON CONFLICT (nombre) DO NOTHING;
INSERT INTO areas (nombre, estado) VALUES ('RECUPERACIÓN PEDAGÓGICA', 'A') ON CONFLICT (nombre) DO NOTHING;
INSERT INTO areas (nombre, estado) VALUES ('TRABAJO SOCIAL', 'A') ON CONFLICT (nombre) DO NOTHING;

-- Insertar Pacientes de prueba
INSERT INTO paciente (ficha, nombres, apellidos, fecha_nacimiento, sexo, cedula_representante, estado) VALUES
('P001', 'María', 'González', '2015-03-15', 'F', '0987654321', 'A') ON CONFLICT (ficha) DO NOTHING;
INSERT INTO paciente (ficha, nombres, apellidos, fecha_nacimiento, sexo, cedula_representante, estado) VALUES
('P002', 'Juan', 'Pérez', '2016-07-22', 'M', '0912345678', 'A') ON CONFLICT (ficha) DO NOTHING;
INSERT INTO paciente (ficha, nombres, apellidos, fecha_nacimiento, sexo, cedula_representante, estado) VALUES
('P003', 'Sofía', 'Rodríguez', '2014-11-10', 'F', '0923456789', 'A') ON CONFLICT (ficha) DO NOTHING;
INSERT INTO paciente (ficha, nombres, apellidos, fecha_nacimiento, sexo, cedula_representante, estado) VALUES
('P004', 'Carlos', 'Martínez', '2017-05-18', 'M', '0934567890', 'A') ON CONFLICT (ficha) DO NOTHING;
INSERT INTO paciente (ficha, nombres, apellidos, fecha_nacimiento, sexo, cedula_representante, estado) VALUES
('P005', 'Ana', 'López', '2015-09-25', 'F', '0945678901', 'A') ON CONFLICT (ficha) DO NOTHING;

-- Asignar usuarios a áreas
INSERT INTO usuarios_areas (id_usuario, id_area, fecha_asignacion, estado) VALUES
(1, 1, CURRENT_TIMESTAMP, 'A') ON CONFLICT DO NOTHING;
INSERT INTO usuarios_areas (id_usuario, id_area, fecha_asignacion, estado) VALUES
(2, 2, CURRENT_TIMESTAMP, 'A') ON CONFLICT DO NOTHING;
INSERT INTO usuarios_areas (id_usuario, id_area, fecha_asignacion, estado) VALUES
(3, 3, CURRENT_TIMESTAMP, 'A') ON CONFLICT DO NOTHING;
INSERT INTO usuarios_areas (id_usuario, id_area, fecha_asignacion, estado) VALUES
(4, 4, CURRENT_TIMESTAMP, 'A') ON CONFLICT DO NOTHING;

-- Crear profesionales
INSERT INTO profesionales (id_usuario, especialidad, estado) VALUES
(3, 'Psicólogo Educativo', 'A') ON CONFLICT DO NOTHING;
INSERT INTO profesionales (id_usuario, especialidad, estado) VALUES
(4, 'Odontólogo', 'A') ON CONFLICT DO NOTHING;

-- Crear secretarias y coordinadores
INSERT INTO secretarias (id_usuario, estado) VALUES (1, 'A') ON CONFLICT DO NOTHING;
INSERT INTO coordinadores (id_usuario, estado) VALUES (2, 'A') ON CONFLICT DO NOTHING;