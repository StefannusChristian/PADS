-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: May 20, 2023 at 12:30 PM
-- Server version: 8.0.31
-- PHP Version: 8.0.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `sales`
--
CREATE DATABASE IF NOT EXISTS `sales` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `sales`;

-- --------------------------------------------------------

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
CREATE TABLE IF NOT EXISTS `cart` (
  `id` int NOT NULL AUTO_INCREMENT,
  `sales_id` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `qty` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `sales_id` (`sales_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `cart`
--

INSERT INTO `cart` (`id`, `sales_id`, `qty`) VALUES
(1, 'salesA', 0),
(2, 'salesB', 0);

-- --------------------------------------------------------

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
CREATE TABLE IF NOT EXISTS `category` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `category`
--

INSERT INTO `category` (`id`, `name`) VALUES
(1, 'beverages'),
(2, 'foods'),
(3, 'cleaning product');

-- --------------------------------------------------------

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
CREATE TABLE IF NOT EXISTS `customer` (
  `username` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `address` varchar(99) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `sales_id` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`username`),
  KEY `sales_id` (`sales_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `customer`
--

INSERT INTO `customer` (`username`, `address`, `sales_id`) VALUES
('cusA', 'SHINJUKU EASTSIDE SQUARE 6-27-30 Shinjuku, Shinjuku-ku, Tokyo 160-8430, Japan', NULL),
('cusB', 'Residence No. 55 Central Luxury Mansion', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `detail_cart`
--

DROP TABLE IF EXISTS `detail_cart`;
CREATE TABLE IF NOT EXISTS `detail_cart` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cart_id` int NOT NULL,
  `product_id` int NOT NULL,
  `qty` int NOT NULL,
  `is_available` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `cart_id` (`cart_id`),
  KEY `product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Triggers `detail_cart`
--
DROP TRIGGER IF EXISTS `update_cart_qty_on_delete_detail_cart_trigger`;
DELIMITER $$
CREATE TRIGGER `update_cart_qty_on_delete_detail_cart_trigger` AFTER DELETE ON `detail_cart` FOR EACH ROW BEGIN  
        DECLARE cart_qty_delete INTEGER;
        SELECT COUNT(*) INTO cart_qty_delete FROM detail_cart WHERE cart_id = OLD.cart_id;
        UPDATE cart SET qty = cart_qty_delete WHERE id = OLD.cart_id;
    END
$$
DELIMITER ;
DROP TRIGGER IF EXISTS `update_cart_qty_on_insert_detail_cart_trigger`;
DELIMITER $$
CREATE TRIGGER `update_cart_qty_on_insert_detail_cart_trigger` AFTER INSERT ON `detail_cart` FOR EACH ROW BEGIN
        DECLARE cart_qty_insert INTEGER;
        SELECT COUNT(*) INTO cart_qty_insert FROM detail_cart WHERE cart_id = NEW.cart_id;
        UPDATE cart SET qty = cart_qty_insert WHERE id = NEW.cart_id;
    END
$$
DELIMITER ;
DROP TRIGGER IF EXISTS `update_detail_cart_is_available_on_update_detail_cart_trigger`;
DELIMITER $$
CREATE TRIGGER `update_detail_cart_is_available_on_update_detail_cart_trigger` BEFORE UPDATE ON `detail_cart` FOR EACH ROW BEGIN
        IF NEW.qty > 0 THEN
            SET NEW.is_available = 1;
        ELSE
            SET NEW.is_available = 0;
        END IF;
    END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `detail_order`
--

DROP TABLE IF EXISTS `detail_order`;
CREATE TABLE IF NOT EXISTS `detail_order` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `product_id` int NOT NULL,
  `qty` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Triggers `detail_order`
--
DROP TRIGGER IF EXISTS `update_product_ordered_qty_on_insert_detail_order_trigger`;
DELIMITER $$
CREATE TRIGGER `update_product_ordered_qty_on_insert_detail_order_trigger` AFTER INSERT ON `detail_order` FOR EACH ROW BEGIN
        UPDATE product p
        SET p.ordered_qty = (
            SELECT SUM(qty) 
            FROM detail_order 
            WHERE order_id IN (
                SELECT id 
                FROM `order` 
                WHERE status IN ('active', 'sent')
            ) AND product_id = NEW.product_id
        )
        WHERE p.id = NEW.product_id;
    END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
CREATE TABLE IF NOT EXISTS `order` (
  `id` int NOT NULL AUTO_INCREMENT,
  `date` varchar(27) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `status` varchar(9) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `total_price` int NOT NULL,
  `sales_id` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `customer_id` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `sales_id` (`sales_id`),
  KEY `customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Triggers `order`
--
DROP TRIGGER IF EXISTS `update_product_ordered_qty_on_update_order_trigger`;
DELIMITER $$
CREATE TRIGGER `update_product_ordered_qty_on_update_order_trigger` AFTER UPDATE ON `order` FOR EACH ROW BEGIN
        UPDATE product p
        SET p.ordered_qty = (
            SELECT SUM(qty)
            FROM detail_order
            WHERE order_id IN (
                SELECT id 
                FROM `order` 
                WHERE status IN ('active', 'sent')
            ) AND product_id = p.id
        )
        WHERE p.id = (SELECT id FROM detail_order WHERE order_id=NEW.id AND product_id = p.id);
    END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
CREATE TABLE IF NOT EXISTS `product` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `price` int NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `available_qty` int NOT NULL,
  `ordered_qty` int NOT NULL,
  `total_qty` int NOT NULL,
  `promo` int NOT NULL,
  `img_link` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `warehouse_id` int NOT NULL,
  `category_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `warehouse_id` (`warehouse_id`),
  KEY `category_id` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `product`
--

INSERT INTO `product` (`id`, `name`, `price`, `description`, `available_qty`, `ordered_qty`, `total_qty`, `promo`, `img_link`, `warehouse_id`, `category_id`) VALUES
(1, 'Indomie Mi Instan ', 3100, 'Mi goreng yang lezat dan nikmat, terbuat dari bahan berkualitas dan rempah rempah terbaik.\r\n', 36, 0, 36, 1, 'https://assets.klikindomaret.com/products/10003517/10003517_1.jpg', 1, 1),
(2, 'Sunlight Pencuci P', 9900, 'Sunlight Jeruk Nipis 100 mampu menghilangkan lemak dengan kekuatan 100 jeruk nipis di tiap kemasannya, secara aktif mengangkat dan menghilangkan lemak membandel, dan juga membersihkan dengan cepat berkat teknologi baru Cepat Bilas.\r\n', 3, 0, 3, 0, 'https://assets.klikindomaret.com/products/20112492/20112492_1.jpg', 1, 3),
(3, 'Bear Brand Susu En', 9800, 'Bear brand terbuat dari 100% susu sapi steril murni. Susu steril dianjurkan untuk setiap kegunaan yang membutuhkan susu dan dapat di konsumsi setiap hari sesuai kebutuhan.\r\n', 6, 0, 6, 0, 'https://assets.klikindomaret.com/promos/20230517_07_00_20230523_23_00/10004906/10004906_1.jpg', 1, 2),
(4, 'Khong Guan Biscuit', 91500, 'Khong guan biskuit dengan kualitas terbaik, berbagai bentuk dan rasa yang enak didalamnya.\r\n', 312, 0, 312, 0, 'https://assets.klikindomaret.com/products/10000360/10000360_1.jpg', 1, 2),
(6, 'Nescafe Coffee Dri', 7000, 'Rasakan sensasi minuman kualitas Ala Caf kapan saja dan dimana saja didalam satu kemasan kaleng Nescaf Ala Caf. Dengan tiga varian rasa baru yaitu Latte, Cappucino, dan Caramel Macchiato, kenikmatan minuman caf kini bisa dinikmati oleh siapa saja. Perpadu', 35, 0, 35, 1, 'https://assets.klikindomaret.com/products/20114494/20114494_1.jpg', 1, 1),
(7, 'So Klin Pembersih ', 10900, 'SO KLIN Pembersih Lantai Sereh Lemon Grass merupakan cairan pembersih lantai yang di rancang khusus untuk memudahkan Anda dalam membersihkan lantai rumah. Cairan pembersih lantai persembahan SOKLIN ini secara efektif membersihkan seluruh permukaan lantai.', 463, 0, 463, 1, 'https://assets.klikindomaret.com/products/20101095/20101095_1.jpg', 1, 2);

--
-- Triggers `product`
--
DROP TRIGGER IF EXISTS `update_detail_cart_qty_on_update_product_trigger`;
DELIMITER $$
CREATE TRIGGER `update_detail_cart_qty_on_update_product_trigger` AFTER UPDATE ON `product` FOR EACH ROW BEGIN
        IF NEW.available_qty != OLD.available_qty THEN
            UPDATE detail_cart cd
            SET cd.qty = CASE
                            WHEN cd.qty > NEW.available_qty THEN NEW.available_qty
                            ELSE cd.qty
                        END
            WHERE cd.product_id = NEW.id;
        END IF;
    END
$$
DELIMITER ;
DROP TRIGGER IF EXISTS `update_product_total_qty_on_update_product_trigger`;
DELIMITER $$
CREATE TRIGGER `update_product_total_qty_on_update_product_trigger` BEFORE UPDATE ON `product` FOR EACH ROW SET NEW.total_qty = NEW.ordered_qty + NEW.available_qty
$$
DELIMITER ;
DROP TRIGGER IF EXISTS `update_warehouse_available_qty_on_update_product_trigger`;
DELIMITER $$
CREATE TRIGGER `update_warehouse_available_qty_on_update_product_trigger` AFTER UPDATE ON `product` FOR EACH ROW BEGIN
        UPDATE warehouse w
        SET w.available_qty = (SELECT SUM(available_qty) FROM `product`);
    END
$$
DELIMITER ;
DROP TRIGGER IF EXISTS `update_warehouse_ordered_qty_on_update_product_trigger`;
DELIMITER $$
CREATE TRIGGER `update_warehouse_ordered_qty_on_update_product_trigger` AFTER UPDATE ON `product` FOR EACH ROW BEGIN
        UPDATE warehouse w
        SET w.ordered_qty = (SELECT SUM(ordered_qty) FROM `product`);
    END
$$
DELIMITER ;
DROP TRIGGER IF EXISTS `update_warehouse_total_qty_on_update_product_trigger`;
DELIMITER $$
CREATE TRIGGER `update_warehouse_total_qty_on_update_product_trigger` AFTER UPDATE ON `product` FOR EACH ROW BEGIN
        UPDATE warehouse w
        SET w.total_qty = (SELECT SUM(total_qty) FROM `product`);
    END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `sales`
--

DROP TABLE IF EXISTS `sales`;
CREATE TABLE IF NOT EXISTS `sales` (
  `username` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(27) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(27) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `verified` tinyint(1) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `sales`
--

INSERT INTO `sales` (`username`, `password`, `name`, `email`, `verified`) VALUES
('salesA', 'sA', 'Jason', 'jpiay40@students.calvin.ac.', 1),
('salesB', 'sB', 'Caleb', 'cjsonnnnn@gmail.com', 0);

-- --------------------------------------------------------

--
-- Table structure for table `warehouse`
--

DROP TABLE IF EXISTS `warehouse`;
CREATE TABLE IF NOT EXISTS `warehouse` (
  `id` int NOT NULL AUTO_INCREMENT,
  `ordered_qty` int NOT NULL,
  `available_qty` int NOT NULL,
  `total_qty` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `warehouse`
--

INSERT INTO `warehouse` (`id`, `ordered_qty`, `available_qty`, `total_qty`) VALUES
(1, 0, 855, 855);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `cart`
--
ALTER TABLE `cart`
  ADD CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`sales_id`) REFERENCES `sales` (`username`);

--
-- Constraints for table `customer`
--
ALTER TABLE `customer`
  ADD CONSTRAINT `customer_ibfk_1` FOREIGN KEY (`sales_id`) REFERENCES `sales` (`username`);

--
-- Constraints for table `detail_cart`
--
ALTER TABLE `detail_cart`
  ADD CONSTRAINT `detail_cart_ibfk_1` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`id`),
  ADD CONSTRAINT `detail_cart_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

--
-- Constraints for table `detail_order`
--
ALTER TABLE `detail_order`
  ADD CONSTRAINT `detail_order_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`),
  ADD CONSTRAINT `detail_order_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

--
-- Constraints for table `order`
--
ALTER TABLE `order`
  ADD CONSTRAINT `order_ibfk_1` FOREIGN KEY (`sales_id`) REFERENCES `sales` (`username`),
  ADD CONSTRAINT `order_ibfk_2` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`username`);

--
-- Constraints for table `product`
--
ALTER TABLE `product`
  ADD CONSTRAINT `product_ibfk_1` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`),
  ADD CONSTRAINT `product_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
