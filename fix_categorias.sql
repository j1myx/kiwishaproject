-- Script para arreglar la tabla categorias y permitir categoria_id NULL en productos
-- Base de datos: kiwisha_v2

USE kiwisha_v2;

-- Paso 0: Verificar el nombre de la restricción de clave foránea
-- EJECUTA ESTO PRIMERO Y BUSCA EL NOMBRE EN 'CONSTRAINT'
-- SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE 
-- WHERE TABLE_NAME = 'productos' AND COLUMN_NAME = 'categoria_id' 
-- AND CONSTRAINT_SCHEMA = 'kiwisha_v2' AND REFERENCED_TABLE_NAME IS NOT NULL;

-- Paso 1: Eliminar restricciones de claves foráneas (si existen)
-- Usamos IF EXISTS para evitar errores si ya fueron eliminadas

-- Eliminar FK de categoria (puede tener diferentes nombres)
SET @query = (
    SELECT CONCAT('ALTER TABLE productos DROP FOREIGN KEY ', CONSTRAINT_NAME, ';')
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = 'kiwisha_v2' 
    AND TABLE_NAME = 'productos' 
    AND COLUMN_NAME = 'categoria_id'
    AND REFERENCED_TABLE_NAME = 'categorias'
    LIMIT 1
);
SET @query = IFNULL(@query, 'SELECT 1;');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Eliminar FK de productos_imagenes
SET @query = (
    SELECT CONCAT('ALTER TABLE productos_imagenes DROP FOREIGN KEY ', CONSTRAINT_NAME, ';')
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = 'kiwisha_v2' 
    AND TABLE_NAME = 'productos_imagenes' 
    AND COLUMN_NAME = 'producto_id'
    AND REFERENCED_TABLE_NAME = 'productos'
    LIMIT 1
);
SET @query = IFNULL(@query, 'SELECT 1;');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Eliminar FK de pedidos_elementos
SET @query = (
    SELECT CONCAT('ALTER TABLE pedidos_elementos DROP FOREIGN KEY ', CONSTRAINT_NAME, ';')
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = 'kiwisha_v2' 
    AND TABLE_NAME = 'pedidos_elementos' 
    AND COLUMN_NAME = 'producto_id'
    AND REFERENCED_TABLE_NAME = 'productos'
    LIMIT 1
);
SET @query = IFNULL(@query, 'SELECT 1;');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Eliminar FK de productos_paginas
SET @query = (
    SELECT CONCAT('ALTER TABLE productos_paginas DROP FOREIGN KEY ', CONSTRAINT_NAME, ';')
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = 'kiwisha_v2' 
    AND TABLE_NAME = 'productos_paginas' 
    AND COLUMN_NAME = 'producto_id'
    AND REFERENCED_TABLE_NAME = 'productos'
    LIMIT 1
);
SET @query = IFNULL(@query, 'SELECT 1;');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Eliminar FK de reviews
SET @query = (
    SELECT CONCAT('ALTER TABLE reviews DROP FOREIGN KEY ', CONSTRAINT_NAME, ';')
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = 'kiwisha_v2' 
    AND TABLE_NAME = 'reviews' 
    AND COLUMN_NAME = 'producto_id'
    AND REFERENCED_TABLE_NAME = 'productos'
    LIMIT 1
);
SET @query = IFNULL(@query, 'SELECT 1;');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Paso 2: Hacer que categoria_id sea opcional (NULL) en productos
ALTER TABLE productos MODIFY COLUMN categoria_id INT NULL;

-- Paso 3: Modificar categoria_id para que tenga AUTO_INCREMENT
ALTER TABLE categorias MODIFY COLUMN categoria_id INT AUTO_INCREMENT;

-- Paso 3.5: Modificar producto_id para que tenga AUTO_INCREMENT
ALTER TABLE productos MODIFY COLUMN producto_id INT AUTO_INCREMENT;

-- Paso 3.6: Recrear las claves foráneas que apuntan a productos
ALTER TABLE productos_imagenes 
ADD CONSTRAINT FK_imagen_producto_producto 
FOREIGN KEY (producto_id) 
REFERENCES productos(producto_id) 
ON DELETE CASCADE 
ON UPDATE CASCADE;

ALTER TABLE pedidos_elementos 
ADD CONSTRAINT FK_pedido_elemento_producto 
FOREIGN KEY (producto_id) 
REFERENCES productos(producto_id) 
ON DELETE RESTRICT 
ON UPDATE CASCADE;

ALTER TABLE productos_paginas 
ADD CONSTRAINT FK_producto_pagina_producto 
FOREIGN KEY (producto_id) 
REFERENCES productos(producto_id) 
ON DELETE CASCADE 
ON UPDATE CASCADE;

ALTER TABLE reviews 
ADD CONSTRAINT FK_review_producto 
FOREIGN KEY (producto_id) 
REFERENCES productos(producto_id) 
ON DELETE CASCADE 
ON UPDATE CASCADE;

-- Paso 4: Recrear la clave foránea con ON DELETE SET NULL
ALTER TABLE productos 
ADD CONSTRAINT fk_productos_categoria 
FOREIGN KEY (categoria_id) 
REFERENCES categorias(categoria_id) 
ON DELETE SET NULL 
ON UPDATE CASCADE;

-- Paso 5: Insertar categorías de prueba
INSERT INTO categorias (categoria_id, titulo, resumen, creado_en, creado_por) VALUES
(1, 'Textiles', 'Productos textiles artesanales peruanos', NOW(), 1),
(2, 'Cerámica', 'Artesanías en cerámica de alta calidad', NOW(), 1),
(3, 'Joyería', 'Joyería artesanal peruana', NOW(), 1),
(4, 'Decoración', 'Artículos decorativos hechos a mano', NOW(), 1),
(5, 'Accesorios', 'Accesorios y complementos artesanales', NOW(), 1);

-- Paso 6: Verificar las categorías
SELECT * FROM categorias;

-- Paso 7: Verificar la estructura de la tabla productos
SHOW CREATE TABLE productos;
