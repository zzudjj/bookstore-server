-- ========================================
-- 书店系统数据库初始化脚本
-- 数据库名称: bookstore
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- ========================================

-- 创建数据库
DROP DATABASE IF EXISTS  `bookstore`;
CREATE DATABASE IF NOT EXISTS `bookstore` 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE `bookstore`;

-- ========================================
-- 用户相关表
-- ========================================

-- 用户表
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户编号',
  `account` varchar(100) NOT NULL COMMENT '用户账号(邮箱)',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `name` varchar(50) DEFAULT NULL COMMENT '用户姓名',
  `gender` varchar(10) DEFAULT NULL COMMENT '性别',
  `imgUrl` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `info` text COMMENT '个人简介',
  `manage` tinyint(1) DEFAULT '0' COMMENT '是否为管理员',
  `enable` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `registerTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 地址表
CREATE TABLE `address` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '地址编号',
  `account` varchar(100) NOT NULL COMMENT '用户账号',
  `name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `phone` varchar(20) NOT NULL COMMENT '收货人电话',
  `addr` varchar(255) NOT NULL COMMENT '具体地址',
  `label` varchar(50) DEFAULT NULL COMMENT '地址标签',
  `off` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_account` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- 购物车表
CREATE TABLE `cart` (
  `account` varchar(100) NOT NULL COMMENT '用户账号',
  `id` int(11) NOT NULL COMMENT '图书ID',
  `num` int(11) DEFAULT '1' COMMENT '数量',
  `addTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  PRIMARY KEY (`account`, `id`),
  KEY `idx_account` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ========================================
-- 图书相关表
-- ========================================

-- 图书表
CREATE TABLE `book` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '图书编号',
  `bookName` varchar(255) NOT NULL COMMENT '图书名称',
  `author` varchar(255) DEFAULT NULL COMMENT '作者',
  `isbn` varchar(50) NOT NULL COMMENT 'ISBN号',
  `publish` varchar(255) DEFAULT NULL COMMENT '出版社',
  `birthday` timestamp NULL DEFAULT NULL COMMENT '出版时间',
  `marketPrice` decimal(10,2) DEFAULT NULL COMMENT '市场价',
  `price` decimal(10,2) DEFAULT NULL COMMENT '售价',
  `stock` int(11) DEFAULT '0' COMMENT '库存',
  `description` text COMMENT '图书描述',
  `put` tinyint(1) DEFAULT '1' COMMENT '是否上架',
  `rank` int(11) DEFAULT '0' COMMENT '权重值',
  `newProduct` tinyint(1) DEFAULT '0' COMMENT '是否新品',
  `recommend` tinyint(1) DEFAULT '0' COMMENT '是否推荐',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_isbn` (`isbn`),
  KEY `idx_publish` (`publish`),
  KEY `idx_put` (`put`),
  KEY `idx_newProduct` (`newProduct`),
  KEY `idx_recommend` (`recommend`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';

-- 图书图片表
CREATE TABLE `bookimg` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '图片编号',
  `isbn` varchar(50) NOT NULL COMMENT '图书ISBN',
  `imgSrc` varchar(255) NOT NULL COMMENT '图片路径',
  `cover` tinyint(1) DEFAULT '0' COMMENT '是否为封面',
  PRIMARY KEY (`id`),
  KEY `idx_isbn` (`isbn`),
  KEY `idx_cover` (`cover`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书图片表';

-- 图书分类表
CREATE TABLE `booksort` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '分类编号',
  `sortName` varchar(100) NOT NULL COMMENT '分类名称',
  `upperName` varchar(100) DEFAULT '无' COMMENT '上级分类名称',
  `level` varchar(20) DEFAULT NULL COMMENT '分类级别',
  `rank` int(11) DEFAULT '0' COMMENT '排序权重',
  PRIMARY KEY (`id`),
  KEY `idx_upperName` (`upperName`),
  KEY `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书分类表';

-- 图书分类关联表
CREATE TABLE `booksortlist` (
  `bookSortId` int(11) NOT NULL COMMENT '分类ID',
  `bookId` int(11) NOT NULL COMMENT '图书ID',
  PRIMARY KEY (`bookSortId`, `bookId`),
  KEY `idx_bookId` (`bookId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书分类关联表';

-- 出版社表
CREATE TABLE `publish` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '出版社编号',
  `name` varchar(255) NOT NULL COMMENT '出版社名称',
  `showPublish` tinyint(1) DEFAULT '1' COMMENT '是否显示',
  `rank` int(11) DEFAULT '0' COMMENT '排序权重',
  `num` int(11) DEFAULT '0' COMMENT '图书数量',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出版社表';

-- ========================================
-- 书单相关表
-- ========================================

-- 书单主题表
CREATE TABLE `booktopic` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '书单编号',
  `topicName` varchar(255) NOT NULL COMMENT '书单名称',
  `subTitle` varchar(255) DEFAULT NULL COMMENT '副标题',
  `cover` varchar(255) DEFAULT NULL COMMENT '封面图片',
  `rank` int(11) DEFAULT '0' COMMENT '排序权重',
  `put` tinyint(1) DEFAULT '1' COMMENT '是否上架',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书单主题表';

-- 书单图书关联表
CREATE TABLE `subbooktopic` (
  `topicId` int(11) NOT NULL COMMENT '书单ID',
  `bookId` int(11) NOT NULL COMMENT '图书ID',
  `recommendReason` text COMMENT '推荐理由',
  PRIMARY KEY (`topicId`, `bookId`),
  KEY `idx_bookId` (`bookId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书单图书关联表';

-- ========================================
-- 订单相关表
-- ========================================

-- 订单表
CREATE TABLE `bookorder` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单编号',
  `orderId` varchar(50) NOT NULL COMMENT '订单号',
  `account` varchar(100) NOT NULL COMMENT '用户账号',
  `addressId` int(11) NOT NULL COMMENT '收货地址ID',
  `orderTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `shipTime` timestamp NULL DEFAULT NULL COMMENT '发货时间',
  `getTime` timestamp NULL DEFAULT NULL COMMENT '收货时间',
  `evaluateTime` timestamp NULL DEFAULT NULL COMMENT '评价时间',
  `closeTime` timestamp NULL DEFAULT NULL COMMENT '关闭时间',
  `confirmTime` timestamp NULL DEFAULT NULL COMMENT '确认收货时间',
  `orderStatus` varchar(50) DEFAULT '待付款' COMMENT '订单状态',
  `logisticsCompany` int(11) DEFAULT NULL COMMENT '物流公司ID',
  `logisticsNum` varchar(100) DEFAULT NULL COMMENT '物流单号',
  `beUserDelete` tinyint(1) DEFAULT '0' COMMENT '用户是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_orderId` (`orderId`),
  KEY `idx_account` (`account`),
  KEY `idx_orderStatus` (`orderStatus`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单明细表
CREATE TABLE `orderdetail` (
  `orderId` varchar(50) NOT NULL COMMENT '订单号',
  `bookId` int(11) NOT NULL COMMENT '图书ID',
  `num` int(11) NOT NULL COMMENT '购买数量',
  `price` decimal(10,2) NOT NULL COMMENT '购买时单价',
  PRIMARY KEY (`orderId`, `bookId`),
  KEY `idx_bookId` (`bookId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 订单费用表
CREATE TABLE `expense` (
  `orderId` varchar(50) NOT NULL COMMENT '订单号',
  `productTotalMoney` decimal(10,2) DEFAULT '0.00' COMMENT '商品总价',
  `freight` decimal(10,2) DEFAULT '0.00' COMMENT '运费',
  `coupon` int(11) DEFAULT '0' COMMENT '优惠券',
  `activityDiscount` decimal(10,2) DEFAULT '0.00' COMMENT '活动优惠',
  `allPrice` decimal(10,2) DEFAULT '0.00' COMMENT '订单总金额',
  `finallyPrice` decimal(10,2) DEFAULT '0.00' COMMENT '最终实付金额',
  PRIMARY KEY (`orderId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单费用表';

-- ========================================
-- 索引和外键约束
-- ========================================

-- 添加外键约束
ALTER TABLE `address` ADD CONSTRAINT `fk_address_user` FOREIGN KEY (`account`) REFERENCES `user` (`account`) ON DELETE CASCADE;
ALTER TABLE `cart` ADD CONSTRAINT `fk_cart_user` FOREIGN KEY (`account`) REFERENCES `user` (`account`) ON DELETE CASCADE;
ALTER TABLE `cart` ADD CONSTRAINT `fk_cart_book` FOREIGN KEY (`id`) REFERENCES `book` (`id`) ON DELETE CASCADE;
ALTER TABLE `bookimg` ADD CONSTRAINT `fk_bookimg_book` FOREIGN KEY (`isbn`) REFERENCES `book` (`isbn`) ON DELETE CASCADE;
ALTER TABLE `booksortlist` ADD CONSTRAINT `fk_booksortlist_sort` FOREIGN KEY (`bookSortId`) REFERENCES `booksort` (`id`) ON DELETE CASCADE;
ALTER TABLE `booksortlist` ADD CONSTRAINT `fk_booksortlist_book` FOREIGN KEY (`bookId`) REFERENCES `book` (`id`) ON DELETE CASCADE;
ALTER TABLE `subbooktopic` ADD CONSTRAINT `fk_subbooktopic_topic` FOREIGN KEY (`topicId`) REFERENCES `booktopic` (`id`) ON DELETE CASCADE;
ALTER TABLE `subbooktopic` ADD CONSTRAINT `fk_subbooktopic_book` FOREIGN KEY (`bookId`) REFERENCES `book` (`id`) ON DELETE CASCADE;
ALTER TABLE `bookorder` ADD CONSTRAINT `fk_bookorder_user` FOREIGN KEY (`account`) REFERENCES `user` (`account`) ON DELETE CASCADE;
ALTER TABLE `bookorder` ADD CONSTRAINT `fk_bookorder_address` FOREIGN KEY (`addressId`) REFERENCES `address` (`id`);
ALTER TABLE `orderdetail` ADD CONSTRAINT `fk_orderdetail_order` FOREIGN KEY (`orderId`) REFERENCES `bookorder` (`orderId`) ON DELETE CASCADE;
ALTER TABLE `orderdetail` ADD CONSTRAINT `fk_orderdetail_book` FOREIGN KEY (`bookId`) REFERENCES `book` (`id`);
ALTER TABLE `expense` ADD CONSTRAINT `fk_expense_order` FOREIGN KEY (`orderId`) REFERENCES `bookorder` (`orderId`) ON DELETE CASCADE;
