-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versión del servidor:         9.1.0 - MySQL Community Server - GPL
-- SO del servidor:              Win64
-- HeidiSQL Versión:             12.10.0.7000
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Volcando estructura de base de datos para kiwisha_v2
CREATE DATABASE IF NOT EXISTS `kiwisha_v2` /*!40100 DEFAULT CHARACTER SET utf16 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `kiwisha_v2`;

-- Volcando estructura para tabla kiwisha_v2.categorias
CREATE TABLE IF NOT EXISTS `categorias` (
  `categoria_id` int NOT NULL,
  `titulo` varchar(50) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL DEFAULT '',
  `resumen` varchar(50) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL DEFAULT '',
  `creado_por` int NOT NULL,
  `creado_en` datetime NOT NULL,
  `actualizado_por` int DEFAULT NULL,
  `actualizado_en` datetime DEFAULT NULL,
  PRIMARY KEY (`categoria_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla kiwisha_v2.clientes
CREATE TABLE IF NOT EXISTS `clientes` (
  `cliente_id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `telefono` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `direccion` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `creado_por` int NOT NULL,
  `creado_en` datetime NOT NULL,
  `actualizado_por` int DEFAULT NULL,
  `actualizado_en` datetime DEFAULT NULL,
  PRIMARY KEY (`cliente_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla kiwisha_v2.etiquetas
CREATE TABLE IF NOT EXISTS `etiquetas` (
  `etiqueta_id` int NOT NULL,
  `nombre` varchar(50) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL DEFAULT '',
  `creado_por` int NOT NULL,
  `creado_en` datetime NOT NULL,
  `actualizado_por` int DEFAULT NULL,
  `actualizado_en` datetime DEFAULT NULL,
  PRIMARY KEY (`etiqueta_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla kiwisha_v2.paginas
CREATE TABLE IF NOT EXISTS `paginas` (
  `pagina_id` int NOT NULL,
  `titulo` varchar(50) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL DEFAULT '',
  `url` varchar(50) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL DEFAULT '',
  `resumen` varchar(50) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL DEFAULT '',
  `contenido` longtext CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `tipo` enum('BASE','ARTICULOS') CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `publicado` bit(1) NOT NULL,
  `creado_por` int NOT NULL,
  `creado_en` datetime NOT NULL,
  `actualizado_por` int DEFAULT NULL,
  `actualizado_en` datetime DEFAULT NULL,
  PRIMARY KEY (`pagina_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla kiwisha_v2.paginas_etiquetas
CREATE TABLE IF NOT EXISTS `paginas_etiquetas` (
  `pagina_etiqueta_id` int NOT NULL,
  `pagina_id` int NOT NULL,
  `etiqueta_id` int NOT NULL,
  `creado_por` int NOT NULL,
  `creado_en` datetime NOT NULL,
  `actualizado_por` int DEFAULT NULL,
  `actualizado_en` datetime DEFAULT NULL,
  PRIMARY KEY (`pagina_etiqueta_id`) USING BTREE,
  KEY `FK_etiqueta_pagina_pagina` (`pagina_id`),
  KEY `FK_etiqueta_pagina_etiqueta` (`etiqueta_id`),
  CONSTRAINT `FK_etiqueta_pagina_etiqueta` FOREIGN KEY (`etiqueta_id`) REFERENCES `etiquetas` (`etiqueta_id`),
  CONSTRAINT `FK_etiqueta_pagina_pagina` FOREIGN KEY (`pagina_id`) REFERENCES `paginas` (`pagina_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla kiwisha_v2.pagina_imagenes
CREATE TABLE IF NOT EXISTS `pagina_imagenes` (
  `pagina_imagen_id` int NOT NULL,
  `pagina_id` int NOT NULL,
  `nombre` varchar(50) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL DEFAULT '',
  `ruta` varchar(100) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL DEFAULT '',
  `creado_por` int NOT NULL,
  `creado_en` datetime NOT NULL,
  `actualizado_por` int DEFAULT NULL,
  `actualizado_en` datetime DEFAULT NULL,
  PRIMARY KEY (`pagina_imagen_id`) USING BTREE,
  KEY `FK_imagen_pagina_pagina` (`pagina_id`),
  CONSTRAINT `FK_imagen_pagina_pagina` FOREIGN KEY (`pagina_id`) REFERENCES `paginas` (`pagina_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla kiwisha_v2.pedidos
CREATE TABLE IF NOT EXISTS `pedidos` (
  `pedido_id` int NOT NULL AUTO_INCREMENT,
  `cliente_id` int NOT NULL,
  `total` float NOT NULL,
  `creado_por` int NOT NULL,
  `creado_en` datetime NOT NULL,
  `actualizado_por` int DEFAULT NULL,
  `actualizado_en` datetime DEFAULT NULL,
  PRIMARY KEY (`pedido_id`),
  KEY `FK_pedido_cliente` (`cliente_id`),
  CONSTRAINT `FK_pedido_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`cliente_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla kiwisha_v2.pedido_elementos
CREATE TABLE IF NOT EXISTS `pedido_elementos` (
  `pedido_elemento_id` int NOT NULL AUTO_INCREMENT,
  `pedido_id` int NOT NULL,
  `producto_id` int NOT NULL,
  `precio` float NOT NULL,
  `cantidad` int NOT NULL,
  `creado_por` int NOT NULL,
  `creado_en` datetime NOT NULL,
  `actualizado_por` int DEFAULT NULL,
  `actualizado_en` datetime DEFAULT NULL,
  PRIMARY KEY (`pedido_elemento_id`) USING BTREE,
  KEY `FK_elemento_pedido_pedido` (`pedido_id`),
  KEY `FK_elemento_pedido_producto` (`producto_id`),
  CONSTRAINT `FK_elemento_pedido_pedido` FOREIGN KEY (`pedido_id`) REFERENCES `pedidos` (`pedido_id`),
  CONSTRAINT `FK_elemento_pedido_producto` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`producto_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla kiwisha_v2.productos
CREATE TABLE IF NOT EXISTS `productos` (
  `producto_id` int NOT NULL,
  `categoria_id` int NOT NULL,
  `titulo` varchar(50) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL DEFAULT '',
  `resumen` varchar(50) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL DEFAULT '',
  `imagen` varchar(100) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL DEFAULT '',
  `precio` decimal(16,4) NOT NULL,
  `cantidad` int NOT NULL,
  `publicado` bit(1) NOT NULL,
  `creado_por` int NOT NULL,
  `creado_en` datetime NOT NULL,
  `actualizado_por` int DEFAULT NULL,
  `actualizado_en` datetime DEFAULT NULL,
  PRIMARY KEY (`producto_id`) USING BTREE,
  KEY `FK_producto_categoria` (`categoria_id`),
  CONSTRAINT `FK_producto_categoria` FOREIGN KEY (`categoria_id`) REFERENCES `categorias` (`categoria_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=UTF8MB4_UNICODE_CI;

CREATE TABLE IF NOT EXISTS `producto_imagenes` (
  `producto_imagen_id` int NOT NULL,
  `producto_id` int NOT NULL,
  `nombre` varchar(50) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL DEFAULT '',
  `ruta` varchar(100) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL DEFAULT '',
  `creado_por` int NOT NULL,
  `creado_en` datetime NOT NULL,
  `actualizado_por` int DEFAULT NULL,
  `actualizado_en` datetime DEFAULT NULL,
  PRIMARY KEY (`producto_imagen_id`) USING BTREE,
  KEY `FK_imagen_producto_producto` (`producto_id`),
  CONSTRAINT `FK_imagen_producto_producto` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`producto_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla kiwisha_v2.productos_paginas
CREATE TABLE IF NOT EXISTS `productos_paginas` (
  `producto_pagina_id` int NOT NULL,
  `pagina_id` int NOT NULL,
  `producto_id` int NOT NULL,
  `creado_por` int NOT NULL,
  `creado_en` datetime NOT NULL,
  `actualizado_por` int DEFAULT NULL,
  `actualizado_en` datetime DEFAULT NULL,
  PRIMARY KEY (`producto_pagina_id`) USING BTREE,
  KEY `FK_producto_pagina_pagina` (`pagina_id`),
  KEY `FK_producto_pagina_producto` (`producto_id`),
  CONSTRAINT `FK_producto_pagina_pagina` FOREIGN KEY (`pagina_id`) REFERENCES `paginas` (`pagina_id`),
  CONSTRAINT `FK_producto_pagina_producto` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`producto_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla kiwisha_v2.roles
CREATE TABLE IF NOT EXISTS `roles` (
  `rol_id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `creado_por` int NOT NULL,
  `creado_en` datetime NOT NULL,
  `actualizado_por` int DEFAULT NULL,
  `actualizado_en` datetime DEFAULT NULL,
  PRIMARY KEY (`rol_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla kiwisha_v2.roles_usuarios
CREATE TABLE IF NOT EXISTS `roles_usuarios` (
  `rol_usuario_id` int NOT NULL,
  `usuario_id` int NOT NULL,
  `rol_id` int NOT NULL,
  `creado_por` int NOT NULL,
  `creado_en` datetime NOT NULL,
  `actualizado_por` int DEFAULT NULL,
  `actualizado_en` datetime DEFAULT NULL,
  PRIMARY KEY (`rol_usuario_id`) USING BTREE,
  KEY `FK_rol_usuario_usuario` (`usuario_id`),
  KEY `FK_rol_usuario_rol` (`rol_id`),
  CONSTRAINT `FK_rol_usuario_rol` FOREIGN KEY (`rol_id`) REFERENCES `roles` (`rol_id`),
  CONSTRAINT `FK_rol_usuario_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`usuario_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla kiwisha_v2.transacciones
CREATE TABLE IF NOT EXISTS `transacciones` (
  `transaccion_id` int NOT NULL AUTO_INCREMENT,
  `pedido_id` int NOT NULL,
  `codigo` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo` enum('PRESENCIAL','VIRTUAL') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `modo` enum('EFECTIVO','TARJETA') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `estado` bit(1) NOT NULL,
  `creado_por` int NOT NULL,
  `creado_en` datetime NOT NULL,
  `actualizado_por` int DEFAULT NULL,
  `actualizado_en` datetime DEFAULT NULL,
  PRIMARY KEY (`transaccion_id`),
  KEY `FK_transaccion_pedido` (`pedido_id`),
  CONSTRAINT `FK_transaccion_pedido` FOREIGN KEY (`pedido_id`) REFERENCES `pedidos` (`pedido_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla kiwisha_v2.usuarios
CREATE TABLE IF NOT EXISTS `usuarios` (
  `usuario_id` int NOT NULL AUTO_INCREMENT,
  `primer_nombre` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `segundo_nombre` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `apellidos` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre_usuario` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `movil` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `hash_contrasena` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `activo` bit(1) NOT NULL,
  `creado_por` int NOT NULL,
  `creado_en` datetime NOT NULL,
  `actualizado_por` int DEFAULT NULL,
  `actualizado_en` datetime DEFAULT NULL,
  PRIMARY KEY (`usuario_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- La exportación de datos fue deseleccionada.

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
