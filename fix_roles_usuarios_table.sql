-- Script para arreglar la tabla roles_usuarios y asignar el rol ADMIN al usuario admin

USE kiwisha_v2;

-- 1. Ver la estructura actual de la tabla
DESCRIBE roles_usuarios;

-- 2. Modificar el campo rol_usuario_id para que sea AUTO_INCREMENT
ALTER TABLE roles_usuarios 
MODIFY COLUMN rol_usuario_id INT NOT NULL AUTO_INCREMENT;

-- 3. Eliminar cualquier asignación previa del usuario admin (por si acaso)
DELETE FROM roles_usuarios 
WHERE usuario_id = (SELECT usuario_id FROM usuarios WHERE email = 'admin@kiwisha.com');

-- 4. Insertar la asignación del rol ADMIN al usuario admin
INSERT INTO roles_usuarios (usuario_id, rol_id, creado_por, creado_en)
SELECT 
    u.usuario_id,
    r.rol_id,
    1 as creado_por,
    NOW() as creado_en
FROM usuarios u
CROSS JOIN roles r
WHERE u.email = 'admin@kiwisha.com' AND r.nombre = 'ADMIN';

-- 5. Verificar el resultado
SELECT 
    u.usuario_id,
    u.email,
    u.activo,
    ru.rol_usuario_id,
    r.nombre as rol
FROM usuarios u
LEFT JOIN roles_usuarios ru ON u.usuario_id = ru.usuario_id
LEFT JOIN roles r ON ru.rol_id = r.rol_id
WHERE u.email = 'admin@kiwisha.com';

-- 6. Ver la estructura final
DESCRIBE roles_usuarios;
