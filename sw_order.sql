DROP TABLE IF EXISTS `sw_order_line`;
DROP TABLE IF EXISTS `sw_product`;
DROP TABLE IF EXISTS `sw_order`;

CREATE TABLE `sw_order` (
  `order_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `order_name` VARCHAR(255) NOT NULL,
  `order_status` enum ('NEW','PROCESSING','COMPLETE','BACKORDERED')
		DEFAULT "NEW" NOT NULL
		COMMENT 'Order Status',
        
  `_created` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `_modified` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
		ON UPDATE CURRENT_TIMESTAMP,
        
  PRIMARY KEY (`order_id`),
  INDEX `IX_sw_order_status`
		(`order_status`)
		COMMENT 'Lookup orders by status'
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE 
utf8mb4_unicode_ci COMMENT='sw_order';

CREATE TABLE `sw_product` (
  `product_id` int unsigned NOT NULL AUTO_INCREMENT,  

  `product_name` VARCHAR(255) NOT NULL
		COMMENT 'Product name',
  `product_status` enum ('NEW','BACKORDERED')
		DEFAULT "NEW" NOT NULL
		COMMENT 'Product inventory Status',
  `product_qty` INT NOT NULL
		COMMENT 'Qty in inventory',
  `product_qty_bckord` INT DEFAULT NULL
		COMMENT 'Qty back ordered',
        
  `_created` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `_modified` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
		ON UPDATE CURRENT_TIMESTAMP,
        
  PRIMARY KEY (`product_id`),
  INDEX `IX_product_name`
	(`product_name`)
	COMMENT 'Lookup products by product_name'

) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE 
utf8mb4_unicode_ci COMMENT='sw_product';

CREATE TABLE `sw_order_line` (
  `order_line_id` int unsigned NOT NULL AUTO_INCREMENT,
  `sw_order_id` int unsigned NOT NULL,  
  `order_line_status` enum ('NEW','ALLOCATED','BACKORDERED')
		DEFAULT "NEW" NOT NULL
		COMMENT 'Order Line Status',
  `sw_product_id` int unsigned NOT NULL
		COMMENT 'Product Id',
  `order_line_qty` INT NOT NULL
		COMMENT 'Qty ordered',
  `order_line_qty_allct` INT DEFAULT NULL
		COMMENT 'Qty allocated',
  `order_line_qty_bckord` INT DEFAULT NULL
		COMMENT 'Qty back ordered',
        
  `_created` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `_modified` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
		ON UPDATE CURRENT_TIMESTAMP,
        
  PRIMARY KEY (`order_line_id`),
  INDEX `IX_sw_order_id`
	(`sw_order_id`)
	COMMENT 'Lookup order lines by order_id',
    
  CONSTRAINT `FK_sw_order_sw_order_id` FOREIGN KEY (`sw_order_id`)
	REFERENCES `sw_order` (`order_id`)
	ON DELETE RESTRICT ON UPDATE CASCADE,
        
  CONSTRAINT `FK_sw_order_line_sw_product_id`
	FOREIGN KEY (`sw_product_id`)
	REFERENCES `sw_product` (`product_id`)
	ON DELETE RESTRICT ON UPDATE CASCADE

) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE 
utf8mb4_unicode_ci COMMENT='sw_order_line';