-- ========================================
-- 书店系统数据库初始化脚本
-- 数据库名称: bookstore
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- ========================================
DROP DATABASE IF EXISTS `bookstore`;
-- 创建数据库
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

-- 删除旧表，避免冲突
DROP TABLE IF EXISTS `subbooktopic`;
DROP TABLE IF EXISTS `booktopic`;

-- 新书单主题表（Topic）
CREATE TABLE `topic` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '书单ID',
    `title` varchar(255) NOT NULL COMMENT '书单标题',
    `subTitle` varchar(255) DEFAULT NULL COMMENT '副标题',
    `cover` varchar(1024) DEFAULT NULL COMMENT '封面图片',
    `rank` int(11) DEFAULT 0 COMMENT '排序权重',
    `status` tinyint(1) DEFAULT 1 COMMENT '是否上架(1=上架)',
    `viewCnt` int(11) DEFAULT 0 COMMENT '浏览量',
    `favCnt` int(11) DEFAULT 0 COMMENT '收藏量',
    `orderCnt` int(11) DEFAULT 0 COMMENT '成交量',
    `createdAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updatedAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书单表';

-- 书单条目表（Topic Item）
CREATE TABLE `topic_item` (
    `topicId` int(11) NOT NULL COMMENT '书单ID',
    `bookId` int(11) NOT NULL COMMENT '图书ID',
    `recommendReason` text COMMENT '推荐理由',
    `orderNo` int(11) DEFAULT 0 COMMENT '条目排序',
    PRIMARY KEY (`topicId`,`bookId`),
    KEY `idx_bookId` (`bookId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书单条目表';

-- 书单收藏表（Topic Fav）
CREATE TABLE `topic_fav` (
    `userAccount` varchar(100) NOT NULL COMMENT '用户账号',
    `topicId` int(11) NOT NULL COMMENT '书单ID',
    `favAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (`userAccount`,`topicId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书单收藏表';

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
-- 创建缺失的数据库表
-- ========================================

USE `bookstore`;

-- 新品推荐表
CREATE TABLE `newproduct` (
                              `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '新品推荐编号',
                              `bookId` int(11) NOT NULL COMMENT '图书ID',
                              `rank` int(11) DEFAULT '0' COMMENT '推荐权重',
                              `addTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_bookId` (`bookId`),
                              KEY `idx_rank` (`rank`),
                              KEY `idx_addTime` (`addTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='新品推荐表';

-- 推荐图书表
CREATE TABLE `recommend` (
                             `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '推荐编号',
                             `bookId` int(11) NOT NULL COMMENT '图书ID',
                             `rank` int(11) DEFAULT '0' COMMENT '推荐权重',
                             `addTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `uk_bookId` (`bookId`),
                             KEY `idx_rank` (`rank`),
                             KEY `idx_addTime` (`addTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐图书表';

-- 添加外键约束
ALTER TABLE `newproduct` ADD CONSTRAINT `fk_newproduct_book` FOREIGN KEY (`bookId`) REFERENCES `book` (`id`) ON DELETE CASCADE;
ALTER TABLE `recommend` ADD CONSTRAINT `fk_recommend_book` FOREIGN KEY (`bookId`) REFERENCES `book` (`id`) ON DELETE CASCADE;

-- 插入一些示例数据（如果book表中有数据的话）
-- 注意：这些INSERT语句只有在book表中存在对应ID的记录时才会成功

-- 检查是否有图书数据
SELECT COUNT(*) as book_count FROM book;

-- 如果有图书数据，可以添加一些推荐和新品
-- INSERT INTO `newproduct` (`bookId`, `rank`)
-- SELECT `id`, 1 FROM `book` WHERE `newProduct` = 1 LIMIT 10;

-- INSERT INTO `recommend` (`bookId`, `rank`)
-- SELECT `id`, 1 FROM `book` WHERE `recommend` = 1 LIMIT 10;


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
ALTER TABLE `topic_item` ADD CONSTRAINT `fk_topic_item_topic` FOREIGN KEY (`topicId`) REFERENCES `topic` (`id`) ON DELETE CASCADE;
ALTER TABLE `topic_item` ADD CONSTRAINT `fk_topic_item_book` FOREIGN KEY (`bookId`) REFERENCES `book` (`id`) ON DELETE CASCADE;
ALTER TABLE `bookorder` ADD CONSTRAINT `fk_bookorder_user` FOREIGN KEY (`account`) REFERENCES `user` (`account`) ON DELETE CASCADE;
ALTER TABLE `bookorder` ADD CONSTRAINT `fk_bookorder_address` FOREIGN KEY (`addressId`) REFERENCES `address` (`id`);
ALTER TABLE `orderdetail` ADD CONSTRAINT `fk_orderdetail_order` FOREIGN KEY (`orderId`) REFERENCES `bookorder` (`orderId`) ON DELETE CASCADE;
ALTER TABLE `orderdetail` ADD CONSTRAINT `fk_orderdetail_book` FOREIGN KEY (`bookId`) REFERENCES `book` (`id`);
ALTER TABLE `expense` ADD CONSTRAINT `fk_expense_order` FOREIGN KEY (`orderId`) REFERENCES `bookorder` (`orderId`) ON DELETE CASCADE;



-- ========================================
-- 秒杀系统表结构 - 适配现有bookstore系统
-- 执行前请确保已连接到bookstore数据库
-- ========================================

USE `bookstore`;

-- ========================================
-- 1. 秒杀活动表
-- ========================================
CREATE TABLE `spikeActivity` (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '活动ID',
                                 `activityName` varchar(100) NOT NULL COMMENT '活动名称',
                                 `activityDesc` text COMMENT '活动描述',
                                 `startTime` datetime NOT NULL COMMENT '开始时间',
                                 `endTime` datetime NOT NULL COMMENT '结束时间',
                                 `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态：0-未开始，1-进行中，2-已结束，3-已取消',
                                 `createTime` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `updateTime` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 `createdBy` varchar(100) COMMENT '创建人账号',
                                 PRIMARY KEY (`id`),
                                 KEY `idx_startEndTime` (`startTime`, `endTime`),
                                 KEY `idx_status` (`status`),
                                 KEY `idx_createdBy` (`createdBy`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀活动表';

-- ========================================
-- 2. 秒杀商品表
-- ========================================
CREATE TABLE `spikeGoods` (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '秒杀商品ID',
                              `activityId` bigint(20) NOT NULL COMMENT '活动ID',
                              `bookId` int(11) NOT NULL COMMENT '图书ID',
                              `spikePrice` decimal(10,2) NOT NULL COMMENT '秒杀价格',
                              `originalPrice` decimal(10,2) NOT NULL COMMENT '原价',
                              `spikeStock` int(11) NOT NULL COMMENT '秒杀库存',
                              `soldCount` int(11) DEFAULT '0' COMMENT '已售数量',
                              `limitPerUser` int(11) DEFAULT '1' COMMENT '每人限购数量',
                              `sortOrder` int(11) DEFAULT '0' COMMENT '排序',
                              `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0-下架，1-上架',
                              `createTime` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updateTime` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`id`),
                              KEY `idx_activityId` (`activityId`),
                              KEY `idx_bookId` (`bookId`),
                              KEY `idx_status` (`status`),
                              KEY `idx_sortOrder` (`sortOrder`),
                              CONSTRAINT `fk_spikeGoods_activity` FOREIGN KEY (`activityId`) REFERENCES `spikeActivity` (`id`) ON DELETE CASCADE,
                              CONSTRAINT `fk_spikeGoods_book` FOREIGN KEY (`bookId`) REFERENCES `book` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀商品表';

-- ========================================
-- 3. 秒杀订单表
-- ========================================
CREATE TABLE `spikeOrder` (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '秒杀订单ID',
                              `orderId` varchar(50) NOT NULL COMMENT '订单号',
                              `spikeGoodsId` bigint(20) NOT NULL COMMENT '秒杀商品ID',
                              `userAccount` varchar(100) NOT NULL COMMENT '用户账号',
                              `quantity` int(11) NOT NULL DEFAULT '1' COMMENT '购买数量',
                              `spikePrice` decimal(10,2) NOT NULL COMMENT '秒杀价格',
                              `totalAmount` decimal(10,2) NOT NULL COMMENT '总金额',
                              `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态：0-待支付，1-已支付，2-已取消，3-已退款',
                              `payTime` datetime COMMENT '支付时间',
                              `cancelTime` datetime COMMENT '取消时间',
                              `expireTime` datetime NOT NULL COMMENT '过期时间',
                              `createTime` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updateTime` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_orderId` (`orderId`),
                              KEY `idx_spikeGoodsId` (`spikeGoodsId`),
                              KEY `idx_userAccount` (`userAccount`),
                              KEY `idx_status` (`status`),
                              KEY `idx_expireTime` (`expireTime`),
                              KEY `idx_createTime` (`createTime`),
                              CONSTRAINT `fk_spikeOrder_goods` FOREIGN KEY (`spikeGoodsId`) REFERENCES `spikeGoods` (`id`) ON DELETE CASCADE,
                              CONSTRAINT `fk_spikeOrder_user` FOREIGN KEY (`userAccount`) REFERENCES `user` (`account`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀订单表';

-- ========================================
-- 4. 秒杀记录表
-- ========================================
CREATE TABLE `spikeRecord` (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
                               `spikeGoodsId` bigint(20) NOT NULL COMMENT '秒杀商品ID',
                               `userAccount` varchar(100) NOT NULL COMMENT '用户账号',
                               `spikeTime` datetime NOT NULL COMMENT '秒杀时间',
                               `result` tinyint(1) NOT NULL COMMENT '结果：0-失败，1-成功',
                               `failReason` varchar(200) COMMENT '失败原因',
                               `ipAddress` varchar(45) COMMENT 'IP地址',
                               `userAgent` varchar(500) COMMENT '用户代理',
                               PRIMARY KEY (`id`),
                               KEY `idx_spikeGoodsUser` (`spikeGoodsId`, `userAccount`),
                               KEY `idx_spikeTime` (`spikeTime`),
                               KEY `idx_result` (`result`),
                               KEY `idx_userAccount` (`userAccount`),
                               CONSTRAINT `fk_spikeRecord_goods` FOREIGN KEY (`spikeGoodsId`) REFERENCES `spikeGoods` (`id`) ON DELETE CASCADE,
                               CONSTRAINT `fk_spikeRecord_user` FOREIGN KEY (`userAccount`) REFERENCES `user` (`account`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀记录表';

-- ========================================
-- 5. 插入示例数据
-- ========================================

-- 插入秒杀活动
INSERT INTO `spikeActivity` (`activityName`, `activityDesc`, `startTime`, `endTime`, `status`, `createdBy`) VALUES
                                                                                                                ('新年特惠秒杀', '新年期间限时秒杀活动，精选图书超低价', '2024-01-01 10:00:00', '2024-01-01 12:00:00', 1, 'admin@bookstore.com'),
                                                                                                                ('午间秒杀场', '午间休息时间秒杀专场', '2024-01-01 12:00:00', '2024-01-01 14:00:00', 0, 'admin@bookstore.com'),
                                                                                                                ('晚间秒杀场', '晚间黄金时段秒杀活动', '2024-01-01 20:00:00', '2024-01-01 22:00:00', 0, 'admin@bookstore.com');

-- 插入秒杀商品（基于现有的图书数据）
INSERT INTO `spikeGoods` (`activityId`, `bookId`, `spikePrice`, `originalPrice`, `spikeStock`, `limitPerUser`, `sortOrder`) VALUES
                                                                                                                                (1, 1, 19.90, 45.00, 50, 2, 1),  -- 红楼梦
                                                                                                                                (1, 2, 49.90, 98.00, 30, 1, 2),  -- Java核心技术
                                                                                                                                (1, 3, 69.90, 128.00, 20, 1, 3), -- 算法导论
                                                                                                                                (2, 4, 39.90, 69.00, 40, 2, 1),  -- Spring Boot实战
                                                                                                                                (2, 5, 15.90, 38.00, 60, 2, 2),  -- 西游记
                                                                                                                                (3, 1, 22.90, 45.00, 30, 1, 1),  -- 红楼梦（晚场）
                                                                                                                                (3, 2, 59.90, 98.00, 25, 1, 2);  -- Java核心技术（晚场）

-- ========================================
-- 6. 创建索引优化查询性能
-- ========================================

-- 秒杀活动表复合索引
CREATE INDEX `idx_activity_time_status` ON `spikeActivity` (`startTime`, `endTime`, `status`);

-- 秒杀商品表复合索引
CREATE INDEX `idx_goods_activity_status` ON `spikeGoods` (`activityId`, `status`);
CREATE INDEX `idx_goods_book_status` ON `spikeGoods` (`bookId`, `status`);

-- 秒杀订单表复合索引
CREATE INDEX `idx_order_user_time` ON `spikeOrder` (`userAccount`, `createTime`);
CREATE INDEX `idx_order_goods_status` ON `spikeOrder` (`spikeGoodsId`, `status`);

-- 秒杀记录表复合索引
CREATE INDEX `idx_record_user_goods` ON `spikeRecord` (`userAccount`, `spikeGoodsId`);
CREATE INDEX `idx_record_time_result` ON `spikeRecord` (`spikeTime`, `result`);

-- ========================================
-- 7. 验证表创建结果
-- ========================================

-- 查看创建的表
SELECT
    TABLE_NAME as '表名',
    TABLE_COMMENT as '表注释',
    TABLE_ROWS as '行数'
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA = 'bookstore'
  AND TABLE_NAME LIKE 'spike%'
ORDER BY TABLE_NAME;

-- 查看外键约束
SELECT
    TABLE_NAME as '表名',
    COLUMN_NAME as '列名',
    CONSTRAINT_NAME as '约束名',
    REFERENCED_TABLE_NAME as '引用表',
    REFERENCED_COLUMN_NAME as '引用列'
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'bookstore'
  AND TABLE_NAME LIKE 'spike%'
  AND REFERENCED_TABLE_NAME IS NOT NULL
ORDER BY TABLE_NAME, COLUMN_NAME;

-- 查看示例数据
SELECT
    a.id,
    a.activityName,
    a.startTime,
    a.endTime,
    a.status,
    COUNT(g.id) as goodsCount
FROM spikeActivity a
         LEFT JOIN spikeGoods g ON a.id = g.activityId
GROUP BY a.id, a.activityName, a.startTime, a.endTime, a.status
ORDER BY a.startTime;

-- 查看秒杀商品详情
SELECT
    g.id,
    a.activityName,
    b.bookName,
    g.spikePrice,
    g.originalPrice,
    ROUND((g.spikePrice / g.originalPrice) * 10, 1) as discount,
    g.spikeStock,
    g.soldCount,
    g.status
FROM spikeGoods g
         JOIN spikeActivity a ON g.activityId = a.id
         JOIN book b ON g.bookId = b.id
ORDER BY g.activityId, g.sortOrder;

-- ========================================
-- 8. 数据完整性检查
-- ========================================

-- 检查是否有孤立的秒杀商品（引用不存在的图书）
SELECT 'spikeGoods中引用不存在的图书' as checkType, COUNT(*) as count
FROM spikeGoods g
         LEFT JOIN book b ON g.bookId = b.id
WHERE b.id IS NULL;

-- 检查是否有孤立的秒杀商品（引用不存在的活动）
SELECT 'spikeGoods中引用不存在的活动' as checkType, COUNT(*) as count
FROM spikeGoods g
         LEFT JOIN spikeActivity a ON g.activityId = a.id
WHERE a.id IS NULL;

-- 检查用户账号是否存在
SELECT 'spikeActivity中不存在的创建人' as checkType, COUNT(*) as count
FROM spikeActivity a
         LEFT JOIN user u ON a.createdBy = u.account
WHERE a.createdBy IS NOT NULL AND u.account IS NULL;

-- ========================================
-- 9. 性能优化建议
-- ========================================

/*
性能优化建议：

1. 数据库层面：
   - 已创建必要的索引优化查询性能
   - 使用InnoDB引擎支持事务和外键约束
   - 字段类型选择合理，避免浪费存储空间

2. 缓存策略：
   - 秒杀活动信息可以缓存到Redis
   - 秒杀商品库存使用Redis计数器
   - 用户购买限制使用Redis记录

3. 并发控制：
   - 使用Redis分布式锁防止超卖
   - 数据库层面使用乐观锁更新库存
   - 限制单用户请求频率

4. 监控指标：
   - 监控秒杀成功率
   - 监控数据库连接数和查询性能
   - 监控Redis内存使用情况
*/

-- 执行完成提示
SELECT '秒杀系统表结构创建完成！' as message, NOW() as createTime;



-- ========================================
-- 检查和添加基础数据
-- ========================================

-- ========================================
-- 书店系统示例数据插入脚本
-- 注意：请先执行 bookstore_ddl.sql 创建表结构
-- ========================================
#
# USE `bookstore`;
#
# -- ========================================
# -- 用户数据
# -- ========================================
#
# -- 插入管理员用户
# INSERT INTO `user` (`account`, `password`, `name`, `gender`, `manage`, `enable`) VALUES
#                                                                                      ('admin@bookstore.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '管理员', '男', 1, 1),
#                                                                                      ('user1@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '张三', '男', 0, 1),
#                                                                                      ('user2@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '李四', '女', 0, 1);
#
# -- 插入地址数据
# INSERT INTO `address` (`account`, `name`, `phone`, `addr`, `label`) VALUES
#                                                                         ('user1@example.com', '张三', '13800138001', '北京市朝阳区某某街道123号', '家'),
#                                                                         ('user1@example.com', '张三', '13800138001', '北京市海淀区某某大厦456号', '公司'),
#                                                                         ('user2@example.com', '李四', '13800138002', '上海市浦东新区某某路789号', '家');
#
# -- ========================================
# -- 图书分类数据
# -- ========================================
#
# -- 一级分类
# INSERT INTO `booksort` (`sortName`, `upperName`, `level`, `rank`) VALUES
#                                                                       ('文学', '无', '级别一', 1),
#                                                                       ('科技', '无', '级别一', 2),
#                                                                       ('教育', '无', '级别一', 3),
#                                                                       ('生活', '无', '级别一', 4);
#
# -- 二级分类
# INSERT INTO `booksort` (`sortName`, `upperName`, `level`, `rank`) VALUES
#                                                                       ('小说', '文学', '级别二', 1),
#                                                                       ('散文', '文学', '级别二', 2),
#                                                                       ('诗歌', '文学', '级别二', 3),
#                                                                       ('计算机', '科技', '级别二', 1),
#                                                                       ('电子', '科技', '级别二', 2),
#                                                                       ('教材', '教育', '级别二', 1),
#                                                                       ('考试', '教育', '级别二', 2),
#                                                                       ('健康', '生活', '级别二', 1),
#                                                                       ('美食', '生活', '级别二', 2);
#
# -- ========================================
# -- 出版社数据
# -- ========================================
#
# INSERT INTO `publish` (`name`, `showPublish`, `rank`) VALUES
#                                                           ('人民文学出版社', 1, 1),
#                                                           ('机械工业出版社', 1, 2),
#                                                           ('清华大学出版社', 1, 3),
#                                                           ('电子工业出版社', 1, 4),
#                                                           ('中信出版社', 1, 5);
#
# -- ========================================
# -- 图书数据
# -- ========================================
#
# INSERT INTO `book` (`bookName`, `author`, `isbn`, `publish`, `birthday`, `marketPrice`, `price`, `stock`, `description`, `put`, `rank`, `newProduct`, `recommend`) VALUES
#                                                                                                                                                                        ('红楼梦', '曹雪芹', '9787020002207', '人民文学出版社', '2020-01-01 00:00:00', 59.00, 45.00, 100, '中国古典文学四大名著之一', 1, 10, 0, 1),
#                                                                                                                                                                        ('Java核心技术', 'Cay S. Horstmann', '9787111213826', '机械工业出版社', '2021-03-15 00:00:00', 128.00, 98.00, 50, 'Java编程经典教程', 1, 9, 1, 1),
#                                                                                                                                                                        ('算法导论', 'Thomas H. Cormen', '9787111407010', '机械工业出版社', '2020-06-01 00:00:00', 158.00, 128.00, 30, '计算机算法经典教材', 1, 8, 0, 1),
#                                                                                                                                                                        ('Spring Boot实战', '汪云飞', '9787121291005', '电子工业出版社', '2021-05-20 00:00:00', 89.00, 69.00, 80, 'Spring Boot开发实战指南', 1, 7, 1, 0),
#                                                                                                                                                                        ('西游记', '吴承恩', '9787020002214', '人民文学出版社', '2020-02-01 00:00:00', 49.00, 38.00, 120, '中国古典文学四大名著之一', 1, 6, 0, 1);
#
# -- ========================================
# -- 图书图片数据
# -- ========================================
#
# INSERT INTO `bookimg` (`isbn`, `imgSrc`, `cover`) VALUES
#                                                       ('9787020002207', 'static/image/book/hongloumeng_cover.jpg', 1),
#                                                       ('9787020002207', 'static/image/book/hongloumeng_1.jpg', 0),
#                                                       ('9787111213826', 'static/image/book/java_cover.jpg', 1),
#                                                       ('9787111213826', 'static/image/book/java_1.jpg', 0),
#                                                       ('9787111407010', 'static/image/book/algorithm_cover.jpg', 1),
#                                                       ('9787121291005', 'static/image/book/springboot_cover.jpg', 1),
#                                                       ('9787020002214', 'static/image/book/xiyouji_cover.jpg', 1);
#
# -- ========================================
# -- 图书分类关联数据
# -- ========================================
#
# INSERT INTO `booksortlist` (`bookSortId`, `bookId`) VALUES
#                                                         (5, 1),  -- 红楼梦 -> 小说
#                                                         (5, 5),  -- 西游记 -> 小说
#                                                         (8, 2),  -- Java核心技术 -> 计算机
#                                                         (8, 3),  -- 算法导论 -> 计算机
#                                                         (8, 4);  -- Spring Boot实战 -> 计算机
#
# -- ========================================
# -- 书单数据
# -- ========================================
#
# INSERT INTO `booktopic` (`topicName`, `subTitle`, `cover`, `rank`, `put`) VALUES
#                                                                               ('程序员必读书单', '提升编程技能的经典书籍', 'static/image/topic/programmer_books.jpg', 1, 1),
#                                                                               ('古典文学精选', '传承千年的文学瑰宝', 'static/image/topic/classic_literature.jpg', 2, 1);
#
# INSERT INTO `subbooktopic` (`topicId`, `bookId`, `recommendReason`) VALUES
#                                                                         (1, 2, 'Java开发者的必备参考书，内容全面深入'),
#                                                                         (1, 3, '算法学习的经典教材，计算机科学基础'),
#                                                                         (1, 4, '现代Java开发框架实战指南'),
#                                                                         (2, 1, '中国古典小说的巅峰之作，文学价值极高'),
#                                                                         (2, 5, '神话色彩浓厚的古典小说，想象力丰富');
#
# -- ========================================
# -- 购物车示例数据
# -- ========================================
#
# INSERT INTO `cart` (`account`, `id`, `num`) VALUES
#                                                 ('user1@example.com', 1, 2),
#                                                 ('user1@example.com', 2, 1),
#                                                 ('user2@example.com', 3, 1);
#
# -- ========================================
# -- 订单示例数据
# -- ========================================
#
# INSERT INTO `bookorder` (`orderId`, `account`, `addressId`, `orderTime`, `orderStatus`) VALUES
#                                                                                             ('ORD202401010001', 'user1@example.com', 1, '2024-01-01 10:30:00', '已完成'),
#                                                                                             ('ORD202401020001', 'user2@example.com', 3, '2024-01-02 14:20:00', '待发货');
#
# INSERT INTO `orderdetail` (`orderId`, `bookId`, `num`, `price`) VALUES
#                                                                     ('ORD202401010001', 1, 1, 45.00),
#                                                                     ('ORD202401010001', 2, 1, 98.00),
#                                                                     ('ORD202401020001', 3, 1, 128.00);
#
# INSERT INTO `expense` (`orderId`, `productTotalMoney`, `freight`, `coupon`, `activityDiscount`, `allPrice`, `finallyPrice`) VALUES
#                                                                                                                                 ('ORD202401010001', 143.00, 0.00, 0, 0.00, 143.00, 143.00),
#                                                                                                                                 ('ORD202401020001', 128.00, 0.00, 0, 0.00, 128.00, 128.00);
#
# -- ========================================
# -- 更新出版社图书数量
# -- ========================================
#
# UPDATE `publish` SET `num` = (
#     SELECT COUNT(*) FROM `book` WHERE `book`.`publish` = `publish`.`name`
# ) WHERE `id` > 0;
#
#

-- ========================================
-- 公告与网站介绍相关表
-- ========================================

-- 公告表
CREATE TABLE `announcement` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '公告编号',
    `title` VARCHAR(255) NOT NULL COMMENT '公告标题',
    `content` TEXT NOT NULL COMMENT '公告内容',
    `author` VARCHAR(100) DEFAULT NULL COMMENT '发布人账号',
    `publishTime` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `enable` TINYINT(1) DEFAULT '1' COMMENT '是否展示 (1=展示,0=隐藏)',
    PRIMARY KEY (`id`),
    KEY `idx_enable` (`enable`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网站公告表';

-- 网站介绍表 (理论上只保存一条记录)
CREATE TABLE `about` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID (固定为1)',
    `content` TEXT NOT NULL COMMENT '网站介绍内容',
    `updateTime` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网站介绍信息表';
