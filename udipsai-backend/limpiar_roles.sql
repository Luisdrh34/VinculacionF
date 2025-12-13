-- Script para limpiar roles duplicados
-- Ejecuta este script en tu cliente de base de datos (Workbench, etc)

SET SQL_SAFE_UPDATES = 0;

-- 1. Crear tabla temporal con los IDs de roles que queremos mantener (el menor ID para cada nombre)
CREATE TEMPORARY TABLE keep_roles AS
SELECT MIN(id_rol) as id_rol, nombre
FROM roles
GROUP BY nombre;

-- 2. Actualizar las referencias en usuarios_roles para que apunten a los roles válidos
UPDATE usuarios_roles ur
JOIN roles r ON ur.id_rol = r.id_rol
JOIN keep_roles kr ON r.nombre = kr.nombre
SET ur.id_rol = kr.id_rol
WHERE ur.id_rol <> kr.id_rol;

-- (Opcional) Actualizar otras tablas que refieran a id_rol si existen

-- 3. Eliminar los roles duplicados (aquellos que no están en la lista de mantener)
DELETE FROM roles
WHERE id_rol NOT IN (SELECT id_rol FROM keep_roles);

-- 4. Limpiar
DROP TEMPORARY TABLE keep_roles;

SET SQL_SAFE_UPDATES = 1;
