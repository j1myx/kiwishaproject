-- Script simplificado para arreglar AUTO_INCREMENT
-- Base de datos: kiwisha_v2
-- Ejecutar línea por línea o en bloques pequeños

USE kiwisha_v2;

-- ==================================================
-- PARTE 1: Modificar producto_id para AUTO_INCREMENT
-- ==================================================

-- PASO 1: Eliminar las 3 FK que apuntan a productos
ALTER TABLE carrito_items DROP FOREIGN KEY FK_carrito_producto;
ALTER TABLE pedido_elementos DROP FOREIGN KEY FK_elemento_pedido_producto;
ALTER TABLE productos_imagenes DROP FOREIGN KEY FK_imagen_producto_producto;

-- PASO 2: Ahora sí podemos modificar producto_id
ALTER TABLE productos MODIFY COLUMN producto_id INT AUTO_INCREMENT;

-- PASO 3: Recrear las FK con CASCADE para mejor manejo
ALTER TABLE carrito_items
ADD CONSTRAINT FK_carrito_producto
FOREIGN KEY (producto_id)
REFERENCES productos(producto_id)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE pedido_elementos
ADD CONSTRAINT FK_elemento_pedido_producto
FOREIGN KEY (producto_id)
REFERENCES productos(producto_id)
ON DELETE RESTRICT
ON UPDATE CASCADE;

ALTER TABLE productos_imagenes
ADD CONSTRAINT FK_imagen_producto_producto
FOREIGN KEY (producto_id)
REFERENCES productos(producto_id)
ON DELETE CASCADE
ON UPDATE CASCADE;

-- ==================================================
-- PARTE 2: Verificar que todo quedó bien
-- ==================================================

-- Ver la estructura de productos
SHOW CREATE TABLE productos;

-- Intentar insertar un producto de prueba
INSERT INTO productos (titulo, precio, cantidad, estado, publicado, creado_en, creado_por)
VALUES ('Test AUTO_INCREMENT', 100.00, 10, 'PUBLICADO', 1, NOW(), 1);

-- Ver el producto insertado
SELECT * FROM productos ORDER BY producto_id DESC LIMIT 1;

-- Eliminar el producto de prueba
DELETE FROM productos WHERE titulo = 'Test AUTO_INCREMENT';
