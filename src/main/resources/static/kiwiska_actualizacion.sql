-- ============================================
-- SCRIPT DE ACTUALIZACIÓN DE BASE DE DATOS
-- Agrega las tablas faltantes identificadas
-- ============================================

USE `kiwisha_v2`;

-- Tabla de Items del Carrito
CREATE TABLE IF NOT EXISTS `carrito_items` (
  `carrito_item_id` int NOT NULL AUTO_INCREMENT,
  `sesion_id` varchar(100) NOT NULL,
  `producto_id` int NOT NULL,
  `cliente_id` int DEFAULT NULL,
  `cantidad` int NOT NULL DEFAULT 1,
  `precio` decimal(16,4) NOT NULL,
  `creado_por` int NOT NULL,
  `creado_en` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `actualizado_por` int DEFAULT NULL,
  `actualizado_en` datetime DEFAULT NULL,
  PRIMARY KEY (`carrito_item_id`),
  KEY `FK_carrito_producto` (`producto_id`),
  KEY `FK_carrito_cliente` (`cliente_id`),
  KEY `idx_carrito_sesion` (`sesion_id`),
  CONSTRAINT `FK_carrito_producto` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`producto_id`),
  CONSTRAINT `FK_carrito_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`cliente_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de Métodos de Envío
CREATE TABLE IF NOT EXISTS `metodos_envio` (
  `metodo_envio_id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` TEXT,
  `costo` decimal(10,2) NOT NULL,
  `dias_estimados` int DEFAULT NULL,
  `activo` bit(1) NOT NULL DEFAULT b'1',
  `tipo` enum('ESTANDAR','EXPRESS','INTERNACIONAL','RECOJO_EN_TIENDA') NOT NULL DEFAULT 'ESTANDAR',
  `creado_por` int NOT NULL,
  `creado_en` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `actualizado_por` int DEFAULT NULL,
  `actualizado_en` datetime DEFAULT NULL,
  PRIMARY KEY (`metodo_envio_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de Direcciones de Envío
CREATE TABLE IF NOT EXISTS `direcciones_envio` (
  `direccion_id` int NOT NULL AUTO_INCREMENT,
  `cliente_id` int NOT NULL,
  `nombre_completo` varchar(200) NOT NULL,
  `telefono` varchar(20) NOT NULL,
  `direccion` TEXT NOT NULL,
  `ciudad` varchar(100) NOT NULL,
  `provincia` varchar(100) DEFAULT NULL,
  `codigo_postal` varchar(20) DEFAULT NULL,
  `pais` varchar(100) NOT NULL,
  `es_principal` bit(1) DEFAULT b'0',
  `activo` bit(1) NOT NULL DEFAULT b'1',
  `referencia` TEXT,
  `creado_por` int NOT NULL,
  `creado_en` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `actualizado_por` int DEFAULT NULL,
  `actualizado_en` datetime DEFAULT NULL,
  PRIMARY KEY (`direccion_id`),
  KEY `FK_direccion_cliente` (`cliente_id`),
  CONSTRAINT `FK_direccion_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`cliente_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de Cupones
CREATE TABLE IF NOT EXISTS `cupones` (
  `cupon_id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(50) NOT NULL UNIQUE,
  `descripcion` TEXT,
  `tipo_descuento` enum('PORCENTAJE','MONTO_FIJO') NOT NULL,
  `valor_descuento` decimal(10,2) NOT NULL,
  `compra_minima` decimal(10,2) DEFAULT NULL,
  `descuento_maximo` decimal(10,2) DEFAULT NULL,
  `fecha_inicio` datetime NOT NULL,
  `fecha_fin` datetime NOT NULL,
  `cantidad_maxima_usos` int DEFAULT NULL,
  `cantidad_usos_actual` int DEFAULT 0,
  `uso_por_cliente` int DEFAULT 1,
  `activo` bit(1) NOT NULL DEFAULT b'1',
  `creado_por` int NOT NULL,
  `creado_en` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `actualizado_por` int DEFAULT NULL,
  `actualizado_en` datetime DEFAULT NULL,
  PRIMARY KEY (`cupon_id`),
  KEY `idx_cupon_codigo` (`codigo`),
  KEY `idx_cupon_fechas` (`fecha_inicio`, `fecha_fin`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de Reviews/Reseñas
CREATE TABLE IF NOT EXISTS `reviews` (
  `review_id` int NOT NULL AUTO_INCREMENT,
  `producto_id` int NOT NULL,
  `cliente_id` int NOT NULL,
  `calificacion` int NOT NULL CHECK (`calificacion` BETWEEN 1 AND 5),
  `titulo` varchar(200) DEFAULT NULL,
  `comentario` TEXT NOT NULL,
  `aprobado` bit(1) NOT NULL DEFAULT b'0',
  `activo` bit(1) NOT NULL DEFAULT b'1',
  `compra_verificada` bit(1) DEFAULT b'0',
  `util_count` int DEFAULT 0,
  `creado_por` int NOT NULL,
  `creado_en` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `actualizado_por` int DEFAULT NULL,
  `actualizado_en` datetime DEFAULT NULL,
  PRIMARY KEY (`review_id`),
  KEY `FK_review_producto` (`producto_id`),
  KEY `FK_review_cliente` (`cliente_id`),
  KEY `idx_review_calificacion` (`calificacion`),
  CONSTRAINT `FK_review_producto` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`producto_id`),
  CONSTRAINT `FK_review_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`cliente_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de Configuración del Sitio
CREATE TABLE IF NOT EXISTS `configuracion_sitio` (
  `config_id` int NOT NULL AUTO_INCREMENT,
  `clave` varchar(100) NOT NULL UNIQUE,
  `valor` TEXT,
  `descripcion` varchar(200) DEFAULT NULL,
  `tipo` enum('TEXTO','TEXTAREA','HTML','URL','EMAIL','NUMERO','BOOLEAN','IMAGEN','JSON') NOT NULL DEFAULT 'TEXTO',
  `activo` bit(1) NOT NULL DEFAULT b'1',
  `editable` bit(1) NOT NULL DEFAULT b'1',
  `creado_por` int NOT NULL,
  `creado_en` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `actualizado_por` int DEFAULT NULL,
  `actualizado_en` datetime DEFAULT NULL,
  PRIMARY KEY (`config_id`),
  KEY `idx_config_clave` (`clave`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- DATOS INICIALES PARA CONFIGURACIÓN DEL SITIO
-- ============================================

INSERT INTO `configuracion_sitio` (`clave`, `valor`, `descripcion`, `tipo`, `creado_por`) VALUES
('sitio.nombre', 'Kiwisha - Tesoros Andinos', 'Nombre del sitio web', 'TEXTO', 1),
('sitio.descripcion', 'Productos andinos y derivados de kiwicha de alta calidad', 'Descripción del sitio', 'TEXTAREA', 1),
('empresa.vision', 'Ser la plataforma líder en comercialización de productos andinos', 'Visión de la empresa', 'TEXTAREA', 1),
('empresa.mision', 'Conectar productores andinos con consumidores conscientes', 'Misión de la empresa', 'TEXTAREA', 1),
('contacto.email', 'contacto@kiwisha.com', 'Email de contacto', 'EMAIL', 1),
('contacto.telefono', '+51 999 999 999', 'Teléfono de contacto', 'TEXTO', 1),
('tienda.moneda', 'USD', 'Moneda de la tienda', 'TEXTO', 1),
('tienda.envio.gratis.minimo', '50.00', 'Monto mínimo para envío gratis', 'NUMERO', 1)
ON DUPLICATE KEY UPDATE `actualizado_en` = NOW();

-- ============================================
-- DATOS INICIALES PARA MÉTODOS DE ENVÍO
-- ============================================

INSERT INTO `metodos_envio` (`nombre`, `descripcion`, `costo`, `dias_estimados`, `tipo`, `creado_por`) VALUES
('Envío Estándar', '5-7 días hábiles', 5.00, 6, 'ESTANDAR', 1),
('Envío Express', '2-3 días hábiles', 15.00, 2, 'EXPRESS', 1),
('Recojo en Tienda', 'Disponible en 24 horas', 0.00, 1, 'RECOJO_EN_TIENDA', 1)
ON DUPLICATE KEY UPDATE `actualizado_en` = NOW();

-- ============================================
-- ALTERACIONES A TABLAS EXISTENTES
-- ============================================

-- Nota: MySQL 8.0 no soporta IF NOT EXISTS en ADD COLUMN
-- Si la columna ya existe, simplemente ignorar el error o verificar antes

-- Agregar nuevos campos a la tabla productos (ejecutar solo si no existen)
ALTER TABLE `productos` 
ADD COLUMN `descripcion` TEXT AFTER `resumen`;

ALTER TABLE `productos` 
ADD COLUMN `precio_anterior` decimal(16,4) DEFAULT NULL AFTER `precio`;

ALTER TABLE `productos` 
ADD COLUMN `sku` varchar(50) UNIQUE DEFAULT NULL AFTER `cantidad`;

ALTER TABLE `productos` 
ADD COLUMN `peso` decimal(10,2) DEFAULT NULL AFTER `sku`;

ALTER TABLE `productos` 
ADD COLUMN `unidad_medida` varchar(20) DEFAULT NULL AFTER `peso`;

ALTER TABLE `productos` 
ADD COLUMN `destacado` bit(1) DEFAULT b'0' AFTER `publicado`;

ALTER TABLE `productos` 
ADD COLUMN `nuevo` bit(1) DEFAULT b'0' AFTER `destacado`;

ALTER TABLE `productos` 
ADD COLUMN `en_oferta` bit(1) DEFAULT b'0' AFTER `nuevo`;

ALTER TABLE `productos` 
ADD COLUMN `meta_titulo` varchar(200) DEFAULT NULL AFTER `en_oferta`;

ALTER TABLE `productos` 
ADD COLUMN `meta_descripcion` varchar(500) DEFAULT NULL AFTER `meta_titulo`;

ALTER TABLE `productos` 
ADD COLUMN `slug` varchar(200) UNIQUE DEFAULT NULL AFTER `meta_descripcion`;

-- Agregar índices para mejorar el rendimiento
ALTER TABLE `productos` 
ADD INDEX `idx_producto_destacado` (`destacado`);

ALTER TABLE `productos` 
ADD INDEX `idx_producto_nuevo` (`nuevo`);

ALTER TABLE `productos` 
ADD INDEX `idx_producto_oferta` (`en_oferta`);

-- Nota: Los campos de auditoría (categoria_id, etiqueta_id, pagina_id, producto_id) 
-- ya están configurados correctamente como AUTO_INCREMENT desde la creación inicial.
-- No es necesario modificarlos.

COMMIT;
