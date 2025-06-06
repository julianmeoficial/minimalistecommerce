-- MySQL dump 10.13  Distrib 8.0.37, for Win64 (x86_64)
--
-- Host: localhost    Database: mecommerces_db
-- ------------------------------------------------------
-- Server version	8.0.37

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admin_detalles`
--

DROP TABLE IF EXISTS `admin_detalles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_detalles` (
  `usuario_id` bigint NOT NULL,
  `region` varchar(100) DEFAULT NULL,
  `nivel_acceso` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`usuario_id`),
  CONSTRAINT `admin_detalles_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`usuario_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_detalles`
--

LOCK TABLES `admin_detalles` WRITE;
/*!40000 ALTER TABLE `admin_detalles` DISABLE KEYS */;
/*!40000 ALTER TABLE `admin_detalles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `carrito_compra`
--

DROP TABLE IF EXISTS `carrito_compra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carrito_compra` (
  `carrito_id` bigint NOT NULL AUTO_INCREMENT,
  `usuario_id` bigint NOT NULL,
  `fecha_creacion` datetime NOT NULL,
  `activo` tinyint(1) NOT NULL,
  PRIMARY KEY (`carrito_id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `carrito_compra_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`usuario_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carrito_compra`
--

LOCK TABLES `carrito_compra` WRITE;
/*!40000 ALTER TABLE `carrito_compra` DISABLE KEYS */;
/*!40000 ALTER TABLE `carrito_compra` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `carrito_item`
--

DROP TABLE IF EXISTS `carrito_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carrito_item` (
  `item_id` bigint NOT NULL AUTO_INCREMENT,
  `carrito_id` bigint NOT NULL,
  `producto_id` bigint NOT NULL,
  `cantidad` int NOT NULL,
  `precio_unitario` double NOT NULL,
  PRIMARY KEY (`item_id`),
  KEY `carrito_id` (`carrito_id`),
  KEY `producto_id` (`producto_id`),
  CONSTRAINT `carrito_item_ibfk_1` FOREIGN KEY (`carrito_id`) REFERENCES `carrito_compra` (`carrito_id`),
  CONSTRAINT `carrito_item_ibfk_2` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`producto_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carrito_item`
--

LOCK TABLES `carrito_item` WRITE;
/*!40000 ALTER TABLE `carrito_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `carrito_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categoria_producto`
--

DROP TABLE IF EXISTS `categoria_producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categoria_producto` (
  `categoria_id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `categoria_padre_id` bigint DEFAULT NULL,
  PRIMARY KEY (`categoria_id`),
  UNIQUE KEY `nombre` (`nombre`),
  KEY `categoria_padre_id` (`categoria_padre_id`),
  CONSTRAINT `categoria_producto_ibfk_1` FOREIGN KEY (`categoria_padre_id`) REFERENCES `categoria_producto` (`categoria_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categoria_producto`
--

LOCK TABLES `categoria_producto` WRITE;
/*!40000 ALTER TABLE `categoria_producto` DISABLE KEYS */;
INSERT INTO `categoria_producto` VALUES (1,'ELECTRONICA',NULL,NULL),(2,'ROPA',NULL,NULL),(3,'HOGAR',NULL,NULL),(4,'BELLEZA',NULL,NULL),(5,'DEPORTES',NULL,NULL),(6,'LIBROS',NULL,NULL),(7,'JUGUETES',NULL,NULL),(8,'ALIMENTOS',NULL,NULL),(9,'Smartphones',NULL,1),(10,'Laptops',NULL,1),(11,'Tablets',NULL,1);
/*!40000 ALTER TABLE `categoria_producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comprador_detalles`
--

DROP TABLE IF EXISTS `comprador_detalles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comprador_detalles` (
  `usuario_id` bigint NOT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `preferencias` text,
  `direccion_envio` varchar(255) DEFAULT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`usuario_id`),
  CONSTRAINT `comprador_detalles_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`usuario_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comprador_detalles`
--

LOCK TABLES `comprador_detalles` WRITE;
/*!40000 ALTER TABLE `comprador_detalles` DISABLE KEYS */;
/*!40000 ALTER TABLE `comprador_detalles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orden`
--

DROP TABLE IF EXISTS `orden`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orden` (
  `orden_id` bigint NOT NULL AUTO_INCREMENT,
  `fecha_creacion` datetime NOT NULL,
  `estado` varchar(50) NOT NULL,
  `total` double NOT NULL,
  `usuario_id` bigint NOT NULL,
  PRIMARY KEY (`orden_id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `orden_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`usuario_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orden`
--

LOCK TABLES `orden` WRITE;
/*!40000 ALTER TABLE `orden` DISABLE KEYS */;
/*!40000 ALTER TABLE `orden` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orden_detalle`
--

DROP TABLE IF EXISTS `orden_detalle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orden_detalle` (
  `detalle_id` bigint NOT NULL AUTO_INCREMENT,
  `orden_id` bigint NOT NULL,
  `producto_id` bigint NOT NULL,
  `cantidad` int NOT NULL,
  `precio_unitario` double NOT NULL,
  `subtotal` double NOT NULL,
  PRIMARY KEY (`detalle_id`),
  KEY `orden_id` (`orden_id`),
  KEY `producto_id` (`producto_id`),
  CONSTRAINT `orden_detalle_ibfk_1` FOREIGN KEY (`orden_id`) REFERENCES `orden` (`orden_id`),
  CONSTRAINT `orden_detalle_ibfk_2` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`producto_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orden_detalle`
--

LOCK TABLES `orden_detalle` WRITE;
/*!40000 ALTER TABLE `orden_detalle` DISABLE KEYS */;
/*!40000 ALTER TABLE `orden_detalle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permiso`
--

DROP TABLE IF EXISTS `permiso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permiso` (
  `permiso_id` bigint NOT NULL AUTO_INCREMENT,
  `codigo` varchar(50) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`permiso_id`),
  UNIQUE KEY `codigo` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permiso`
--

LOCK TABLES `permiso` WRITE;
/*!40000 ALTER TABLE `permiso` DISABLE KEYS */;
INSERT INTO `permiso` VALUES (1,'PRODUCTO_CREAR','Crear nuevos productos'),(2,'USUARIO_EDITAR','Modificar informaci├│n de usuarios'),(3,'PEDIDO_VER','Visualizar ├│rdenes de compra');
/*!40000 ALTER TABLE `permiso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto` (
  `producto_id` bigint NOT NULL AUTO_INCREMENT,
  `producto_nombre` varchar(100) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `precio` double NOT NULL,
  `stock` int NOT NULL,
  `categoria_id` bigint NOT NULL,
  `vendedor_id` bigint NOT NULL,
  PRIMARY KEY (`producto_id`),
  KEY `vendedor_id` (`vendedor_id`),
  KEY `producto_categoria_fk` (`categoria_id`),
  CONSTRAINT `producto_categoria_fk` FOREIGN KEY (`categoria_id`) REFERENCES `categoria_producto` (`categoria_id`),
  CONSTRAINT `producto_ibfk_2` FOREIGN KEY (`vendedor_id`) REFERENCES `usuario` (`usuario_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto`
--

LOCK TABLES `producto` WRITE;
/*!40000 ALTER TABLE `producto` DISABLE KEYS */;
INSERT INTO `producto` VALUES (1,'Laptop HP Pavilion','Laptop con procesador Intel Core i5, 8GB RAM, 512GB SSD',899.99,10,15,6),(2,'Smartphone Samsung Galaxy','Smartphone con pantalla AMOLED de 6.5\", 128GB almacenamiento',699.99,15,15,6),(3,'Camiseta Algod├│n','Camiseta 100% algod├│n, disponible en varios colores',19.99,50,16,6);
/*!40000 ALTER TABLE `producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto_imagen`
--

DROP TABLE IF EXISTS `producto_imagen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto_imagen` (
  `imagen_id` bigint NOT NULL AUTO_INCREMENT,
  `url` varchar(255) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `es_principal` tinyint(1) DEFAULT NULL,
  `producto_id` bigint NOT NULL,
  PRIMARY KEY (`imagen_id`),
  KEY `producto_id` (`producto_id`),
  CONSTRAINT `producto_imagen_ibfk_1` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`producto_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto_imagen`
--

LOCK TABLES `producto_imagen` WRITE;
/*!40000 ALTER TABLE `producto_imagen` DISABLE KEYS */;
INSERT INTO `producto_imagen` VALUES (1,'https://example.com/images/laptop.jpg','Imagen principal de laptop',1,1),(2,'https://example.com/images/smartphone.jpg','Imagen principal de smartphone',1,2),(3,'https://example.com/images/camiseta.jpg','Imagen principal de camiseta',1,3);
/*!40000 ALTER TABLE `producto_imagen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rol_permiso`
--

DROP TABLE IF EXISTS `rol_permiso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol_permiso` (
  `rol_id` bigint NOT NULL,
  `permiso_id` bigint NOT NULL,
  PRIMARY KEY (`rol_id`,`permiso_id`),
  KEY `permiso_id` (`permiso_id`),
  CONSTRAINT `rol_permiso_ibfk_1` FOREIGN KEY (`rol_id`) REFERENCES `rol_usuario` (`rol_id`),
  CONSTRAINT `rol_permiso_ibfk_2` FOREIGN KEY (`permiso_id`) REFERENCES `permiso` (`permiso_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rol_permiso`
--

LOCK TABLES `rol_permiso` WRITE;
/*!40000 ALTER TABLE `rol_permiso` DISABLE KEYS */;
/*!40000 ALTER TABLE `rol_permiso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rol_usuario`
--

DROP TABLE IF EXISTS `rol_usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol_usuario` (
  `rol_id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`rol_id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rol_usuario`
--

LOCK TABLES `rol_usuario` WRITE;
/*!40000 ALTER TABLE `rol_usuario` DISABLE KEYS */;
INSERT INTO `rol_usuario` VALUES (1,'ADMINISTRADOR',NULL),(2,'COMPRADOR',NULL),(3,'VENDEDOR',NULL);
/*!40000 ALTER TABLE `rol_usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipo`
--

DROP TABLE IF EXISTS `tipo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipo` (
  `tipo_id` bigint NOT NULL AUTO_INCREMENT,
  `tipo_nombre` varchar(50) NOT NULL,
  `tipo_categoria` enum('USUARIO','PRODUCTO','ADMINISTRADOR','COMPRADOR','VENDEDOR','ELECTRONICA','ROPA','HOGAR','BELLEZA','DEPORTES','LIBROS','JUGUETES','ALIMENTOS') NOT NULL,
  PRIMARY KEY (`tipo_id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipo`
--

LOCK TABLES `tipo` WRITE;
/*!40000 ALTER TABLE `tipo` DISABLE KEYS */;
INSERT INTO `tipo` VALUES (1,'Administrador','USUARIO'),(2,'Comprador','USUARIO'),(3,'Vendedor','USUARIO'),(4,'Electr├│nica','PRODUCTO'),(5,'Ropa','PRODUCTO'),(6,'Hogar','PRODUCTO'),(7,'Belleza','PRODUCTO'),(8,'Deportes','PRODUCTO'),(9,'Libros','PRODUCTO'),(10,'Juguetes','PRODUCTO'),(11,'Alimentos','PRODUCTO'),(12,'Administrador','ADMINISTRADOR'),(13,'Comprador','COMPRADOR'),(14,'Vendedor','VENDEDOR'),(15,'Electr├│nica','ELECTRONICA'),(16,'Ropa','ROPA'),(17,'Hogar','HOGAR'),(18,'Administrador','ADMINISTRADOR'),(19,'Comprador','COMPRADOR'),(20,'Vendedor','VENDEDOR'),(21,'Electr├│nica','ELECTRONICA'),(22,'Ropa','ROPA'),(23,'Hogar','HOGAR'),(24,'Administrador','ADMINISTRADOR'),(25,'Comprador','COMPRADOR'),(26,'Vendedor','VENDEDOR'),(27,'Electr├│nica','ELECTRONICA'),(28,'Ropa','ROPA'),(29,'Hogar','HOGAR');
/*!40000 ALTER TABLE `tipo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `usuario_id` bigint NOT NULL AUTO_INCREMENT,
  `usuario_nombre` varchar(50) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `rol_id` bigint NOT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `direccion_envio` text,
  `num_registro_fiscal` varchar(50) DEFAULT NULL,
  `region_responsabilidad` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`usuario_id`),
  UNIQUE KEY `email` (`email`),
  KEY `tipo_id` (`rol_id`),
  CONSTRAINT `usuario_ibfk_1` FOREIGN KEY (`rol_id`) REFERENCES `tipo` (`tipo_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (2,'Julian Espitia','julianespitia@testing.com','$2a$10$.j6.7w5jMCgD7ok.HpWA9ekeciYeVlhWCFlCcr4IJ7eeBO2lSOwd.',1,NULL,NULL,NULL,NULL),(3,'Gabriela Casta├▒eda','gabi@vendedora.com','borrosa25',3,NULL,NULL,NULL,NULL),(4,'Administrador','admin@mecommerces.com','$2a$10$ubmfOL3P4MmH68/Ty9SNf.IxnDaTBhhEaLuOX.QkBcO00GztYHPoW',12,NULL,NULL,NULL,NULL),(5,'Comprador Demo','comprador@mecommerces.com','$2a$10$JTSw41POgD60knGv1fW14OOJ7FbIf2mjWJJzFNV53L9hPd4.RZpzC',13,NULL,NULL,NULL,NULL),(6,'Vendedor Demo','vendedor@mecommerces.com','$2a$10$gj.JR7fas2DkSX.mvJGPSuZJ8UP8ixCfX6w4GLV7ztKkFNce/iOpe',14,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vendedor_detalles`
--

DROP TABLE IF EXISTS `vendedor_detalles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vendedor_detalles` (
  `usuario_id` bigint NOT NULL,
  `rfc` varchar(20) DEFAULT NULL,
  `especialidad` varchar(100) DEFAULT NULL,
  `direccion_comercial` varchar(255) DEFAULT NULL,
  `num_registro_fiscal` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`usuario_id`),
  CONSTRAINT `vendedor_detalles_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`usuario_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vendedor_detalles`
--

LOCK TABLES `vendedor_detalles` WRITE;
/*!40000 ALTER TABLE `vendedor_detalles` DISABLE KEYS */;
/*!40000 ALTER TABLE `vendedor_detalles` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-16 16:03:02
