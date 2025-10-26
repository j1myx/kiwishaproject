-- ============================================
-- FIX: Ampliar columna hash_contrasena y permitir NULLs en campos opcionales
-- BCrypt genera hashes de 60 caracteres
-- ============================================

USE `kiwisha_v2`;

-- Modificar la columna hash_contrasena de la tabla usuarios
ALTER TABLE `usuarios` 
MODIFY COLUMN `hash_contrasena` VARCHAR(255) NOT NULL;

-- Permitir NULL en campos opcionales del usuario
ALTER TABLE `usuarios` 
MODIFY COLUMN `segundo_nombre` VARCHAR(50) NULL;

ALTER TABLE `usuarios` 
MODIFY COLUMN `movil` VARCHAR(15) NULL;

-- Verificar los cambios
DESCRIBE `usuarios`;
