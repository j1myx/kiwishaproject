-- Script para insertar categorías de prueba y hacer la categoría opcional en productos

-- Primero, modificar la tabla productos para permitir categoria_id NULL
ALTER TABLE productos MODIFY COLUMN categoria_id INT NULL;

-- Insertar categorías de prueba
INSERT INTO categorias (titulo, resumen, creado_en, creado_por) VALUES
('Textiles', 'Productos textiles artesanales peruanos', NOW(), 'admin'),
('Cerámica', 'Artesanías en cerámica de alta calidad', NOW(), 'admin'),
('Joyería', 'Joyería artesanal peruana', NOW(), 'admin'),
('Decoración', 'Artículos decorativos hechos a mano', NOW(), 'admin'),
('Accesorios', 'Accesorios y complementos artesanales', NOW(), 'admin');

-- Verificar las categorías insertadas
SELECT * FROM categorias;
