DROP DATABASE IF EXISTS `sw`;
CREATE DATABASE `sw`
	DEFAULT CHARACTER SET utf8mb4
	DEFAULT COLLATE utf8mb4_unicode_ci;
USE `sw`;

SOURCE sw_order.sql
