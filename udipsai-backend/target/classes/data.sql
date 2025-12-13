-- Create View for Professionals by Area (Fix for missing specialists)
DROP TABLE IF EXISTS vista_profesionales_areas;
CREATE OR REPLACE VIEW vista_profesionales_areas AS
SELECT
    p.id_profesional AS id_profesional,
    p.especialidad AS especialidad,
    p.estado AS estado,
    a.id_area AS id_area,
    a.nombre AS nombre_area,
    u.id_usuario AS id_usuario,
    u.cedula AS cedula,
    u.email AS email,
    u.celular AS celular,
    u.nombres AS nombres,
    u.apellidos AS apellidos,
    u.fecha_creacion AS fecha_creacion,
    u.fecha_modificacion AS fecha_modificacion
FROM profesionales p
JOIN usuarios u ON p.id_usuario = u.id_usuario
JOIN usuarios_areas ua ON u.id_usuario = ua.id_usuario
JOIN areas a ON ua.id_area = a.id_area
WHERE p.estado = 'A' AND ua.estado = 'A' AND u.estado = 'A';

-- Insert roles securely (avoid duplicates)
INSERT INTO roles (nombre, estado)
SELECT * FROM (SELECT 'SECRETARIA' as nombre, 'A' as estado) AS tmp
WHERE NOT EXISTS (SELECT nombre FROM roles WHERE nombre = 'SECRETARIA') LIMIT 1;

INSERT INTO roles (nombre, estado)
SELECT * FROM (SELECT 'COORDINADORA' as nombre, 'A' as estado) AS tmp
WHERE NOT EXISTS (SELECT nombre FROM roles WHERE nombre = 'COORDINADORA') LIMIT 1;

INSERT INTO roles (nombre, estado)
SELECT * FROM (SELECT 'DOCTOR' as nombre, 'A' as estado) AS tmp
WHERE NOT EXISTS (SELECT nombre FROM roles WHERE nombre = 'DOCTOR') LIMIT 1;

INSERT INTO roles (nombre, estado)
SELECT * FROM (SELECT 'PASANTES' as nombre, 'A' as estado) AS tmp
WHERE NOT EXISTS (SELECT nombre FROM roles WHERE nombre = 'PASANTES') LIMIT 1;

-- Insert users securely
INSERT INTO usuarios (cedula, contrasenia, estado, email, nombres, apellidos, fecha_creacion, fecha_modificacion)
SELECT * FROM (SELECT '1234567890' as cedula, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' as pass, 'A' as est, 'secretaria@example.com' as mail, 'Ana' as nom, 'Perez' as ape, CURRENT_TIMESTAMP as fc, CURRENT_TIMESTAMP as fm) AS tmp
WHERE NOT EXISTS (SELECT cedula FROM usuarios WHERE cedula = '1234567890') LIMIT 1;

INSERT INTO usuarios (cedula, contrasenia, estado, email, nombres, apellidos, fecha_creacion, fecha_modificacion)
SELECT * FROM (SELECT '2345678901' as cedula, '$2a$10$yE9RpT5pUJe7/VKPQmDdXeKEuT5xN5tHQqXLzKJQYKNZJ5kzJZH6O' as pass, 'A' as est, 'coordinadora@example.com' as mail, 'Beatriz' as nom, 'Lopez' as ape, CURRENT_TIMESTAMP as fc, CURRENT_TIMESTAMP as fm) AS tmp
WHERE NOT EXISTS (SELECT cedula FROM usuarios WHERE cedula = '2345678901') LIMIT 1;

INSERT INTO usuarios (cedula, contrasenia, estado, email, nombres, apellidos, fecha_creacion, fecha_modificacion)
SELECT * FROM (SELECT '3456789012' as cedula, '$2a$10$UqzVmKZLQXJDQQNLMDYlLeEQxqKxZ7GzQX2xq2JL5uLXqXr5sKzKO' as pass, 'A' as est, 'doctor@example.com' as mail, 'Carlos' as nom, 'Garcia' as ape, CURRENT_TIMESTAMP as fc, CURRENT_TIMESTAMP as fm) AS tmp
WHERE NOT EXISTS (SELECT cedula FROM usuarios WHERE cedula = '3456789012') LIMIT 1;

INSERT INTO usuarios (cedula, contrasenia, estado, email, nombres, apellidos, fecha_creacion, fecha_modificacion)
SELECT * FROM (SELECT '4567890123' as cedula, '$2a$10$LwKq5X2VqJWVqJWVqJWVqeHqXqXqXqXqXqXqXqXqXqXqXqXqXqXqXq' as pass, 'A' as est, 'pasante@example.com' as mail, 'David' as nom, 'Martinez' as ape, CURRENT_TIMESTAMP as fc, CURRENT_TIMESTAMP as fm) AS tmp
WHERE NOT EXISTS (SELECT cedula FROM usuarios WHERE cedula = '4567890123') LIMIT 1;

-- Asignar roles (only if not exists)
INSERT INTO usuarios_roles (id_usuario, id_rol, fecha_asignacion, estado)
SELECT 1, 1, CURRENT_TIMESTAMP, 'A'
FROM DUAL
WHERE NOT EXISTS (SELECT * FROM usuarios_roles WHERE id_usuario = 1 AND id_rol = 1)
AND EXISTS (SELECT * FROM usuarios WHERE id_usuario = 1) AND EXISTS (SELECT * FROM roles WHERE id_rol = 1);

INSERT INTO usuarios_roles (id_usuario, id_rol, fecha_asignacion, estado)
SELECT 2, 2, CURRENT_TIMESTAMP, 'A'
FROM DUAL
WHERE NOT EXISTS (SELECT * FROM usuarios_roles WHERE id_usuario = 2 AND id_rol = 2)
AND EXISTS (SELECT * FROM usuarios WHERE id_usuario = 2) AND EXISTS (SELECT * FROM roles WHERE id_rol = 2);

INSERT INTO usuarios_roles (id_usuario, id_rol, fecha_asignacion, estado)
SELECT 3, 3, CURRENT_TIMESTAMP, 'A'
FROM DUAL
WHERE NOT EXISTS (SELECT * FROM usuarios_roles WHERE id_usuario = 3 AND id_rol = 3)
AND EXISTS (SELECT * FROM usuarios WHERE id_usuario = 3) AND EXISTS (SELECT * FROM roles WHERE id_rol = 3);

INSERT INTO usuarios_roles (id_usuario, id_rol, fecha_asignacion, estado)
SELECT 4, 4, CURRENT_TIMESTAMP, 'A'
FROM DUAL
WHERE NOT EXISTS (SELECT * FROM usuarios_roles WHERE id_usuario = 4 AND id_rol = 4)
AND EXISTS (SELECT * FROM usuarios WHERE id_usuario = 4) AND EXISTS (SELECT * FROM roles WHERE id_rol = 4);

-- Areas
INSERT IGNORE INTO areas (nombre, estado) VALUES ('Psicología', 'A'), ('Psicología Educativa', 'A'), ('Psicología Clínica', 'A'), ('Fonoaudiología', 'A'), ('Estimulación Temprana', 'A'), ('Terapia Física', 'A'), ('Trabajo Social', 'A'), ('Odontología', 'A');

UPDATE roles SET nombre = 'DOCTOR' WHERE nombre = 'PROFESIONAL';

-- Pacientes de prueba
INSERT INTO paciente (nombres_apellidos, cedula, fecha_apertura, fecha_nacimiento, celular, ciudad, domicilio)
SELECT * FROM (SELECT 'Juan Perez Lopez' as n, '0104561230' as c, CURRENT_DATE as fa, '1995-05-15' as fn, '0991112223' as cel, 'Cuenca' as ciu, 'Centro Histórico' as dom) AS tmp
WHERE NOT EXISTS (SELECT cedula FROM paciente WHERE cedula = '0104561230') LIMIT 1;

INSERT INTO paciente (nombres_apellidos, cedula, fecha_apertura, fecha_nacimiento, celular, ciudad, domicilio)
SELECT * FROM (SELECT 'Maria Rodriguez Silva' as n, '0103216540' as c, CURRENT_DATE as fa, '1998-08-20' as fn, '0994445556' as cel, 'Cuenca' as ciu, 'Totoracocha' as dom) AS tmp
WHERE NOT EXISTS (SELECT cedula FROM paciente WHERE cedula = '0103216540') LIMIT 1;

INSERT INTO paciente (nombres_apellidos, cedula, fecha_apertura, fecha_nacimiento, celular, ciudad, domicilio)
SELECT * FROM (SELECT 'Pedro Alvarez Gomez' as n, '0105556667' as c, CURRENT_DATE as fa, '2000-02-10' as fn, '0997778889' as cel, 'Cuenca' as ciu, 'El Vergel' as dom) AS tmp
WHERE NOT EXISTS (SELECT cedula FROM paciente WHERE cedula = '0105556667') LIMIT 1;

INSERT INTO paciente (nombres_apellidos, cedula, fecha_apertura, fecha_nacimiento, celular, ciudad, domicilio)
SELECT * FROM (SELECT 'Lucia Fernandez Castro' as n, '0108889991' as c, CURRENT_DATE as fa, '1992-11-30' as fn, '0990001112' as cel, 'Azogues' as ciu, 'Centro' as dom) AS tmp
WHERE NOT EXISTS (SELECT cedula FROM paciente WHERE cedula = '0108889991') LIMIT 1;

INSERT INTO paciente (nombres_apellidos, cedula, fecha_apertura, fecha_nacimiento, celular, ciudad, domicilio)
SELECT * FROM (SELECT 'Sofia Mendez Ruiz' as n, '0301234567' as c, CURRENT_DATE as fa, '2005-07-25' as fn, '0993334445' as cel, 'Cañar' as ciu, 'Calle Larga' as dom) AS tmp
WHERE NOT EXISTS (SELECT cedula FROM paciente WHERE cedula = '0301234567') LIMIT 1;

-- Asignar área principal al Doctor (Usuario 3 - Psicología)
INSERT INTO usuarios_areas (id_usuario, id_area, fecha_asignacion, estado)
SELECT 3, 1, CURRENT_TIMESTAMP, 'A'
FROM DUAL
WHERE NOT EXISTS (SELECT * FROM usuarios_areas WHERE id_usuario = 3 AND id_area = 1)
AND EXISTS (SELECT * FROM usuarios WHERE id_usuario = 3) AND EXISTS (SELECT * FROM areas WHERE id_area = 1);
