-- ========================================
-- 秒杀系统数据库更新脚本 v1.0
-- 适用于现有bookstore系统的秒杀功能升级
-- 执行前请确保已连接到bookstore数据库
-- ========================================

USE `bookstore`;

-- ========================================
-- 1. 检查并创建秒杀活动表
-- ========================================
CREATE TABLE IF NOT EXISTS `spikeactivity` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `activityName` varchar(100) NOT NULL COMMENT '活动名称',
  `activityDesc` text COMMENT '活动描述',
  `startTime` datetime NOT NULL COMMENT '开始时间',
  `endTime` datetime NOT NULL COMMENT '结束时间',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态：0-未开始，1-进行中，2-已结束，3-已取消',
  `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `createdBy` varchar(100) DEFAULT NULL COMMENT '创建人账号',
  PRIMARY KEY (`id`),
  KEY `idx_startEndTime` (`startTime`,`endTime`),
  KEY `idx_status` (`status`),
  KEY `idx_createdBy` (`createdBy`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='秒杀活动表';

-- ========================================
-- 2. 检查并创建秒杀商品表
-- ========================================
CREATE TABLE IF NOT EXISTS `spikegoods` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '秒杀商品ID',
  `activityId` bigint NOT NULL COMMENT '活动ID',
  `bookId` int NOT NULL COMMENT '图书ID',
  `spikePrice` decimal(10,2) NOT NULL COMMENT '秒杀价格',
  `originalPrice` decimal(10,2) NOT NULL COMMENT '原价',
  `spikeStock` int NOT NULL COMMENT '秒杀库存',
  `soldCount` int DEFAULT '0' COMMENT '已售数量',
  `limitPerUser` int DEFAULT '1' COMMENT '每人限购数量',
  `sortOrder` int DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0-下架，1-上架',
  `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_activityId` (`activityId`),
  KEY `idx_bookId` (`bookId`),
  KEY `idx_status` (`status`),
  KEY `idx_sortOrder` (`sortOrder`),
  CONSTRAINT `fk_spikeGoods_activity` FOREIGN KEY (`activityId`) REFERENCES `spikeactivity` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_spikeGoods_book` FOREIGN KEY (`bookId`) REFERENCES `book` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='秒杀商品表';

-- ========================================
-- 3. 检查并创建秒杀记录表（用于限购检查和行为分析）
-- ========================================
CREATE TABLE IF NOT EXISTS `spikerecord` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `spikeGoodsId` bigint NOT NULL COMMENT '秒杀商品ID',
  `userAccount` varchar(100) NOT NULL COMMENT '用户账号',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '购买数量',
  `spikeTime` datetime NOT NULL COMMENT '秒杀时间',
  `result` tinyint(1) NOT NULL COMMENT '结果：0-失败，1-成功',
  `failReason` varchar(200) DEFAULT NULL COMMENT '失败原因',
  `ipAddress` varchar(45) DEFAULT NULL COMMENT 'IP地址',
  `userAgent` varchar(500) DEFAULT NULL COMMENT '用户代理',
  PRIMARY KEY (`id`),
  KEY `idx_spikeGoodsUser` (`spikeGoodsId`,`userAccount`),
  KEY `idx_spikeTime` (`spikeTime`),
  KEY `idx_result` (`result`),
  KEY `idx_userAccount` (`userAccount`),
  CONSTRAINT `fk_spikeRecord_goods` FOREIGN KEY (`spikeGoodsId`) REFERENCES `spikegoods` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_spikeRecord_user` FOREIGN KEY (`userAccount`) REFERENCES `user` (`account`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='秒杀记录表';

-- ========================================
-- 4. 数据库结构升级检查
-- ========================================

-- 检查spikerecord表是否有quantity字段，如果没有则添加
SET @column_exists = (
  SELECT COUNT(*) 
  FROM INFORMATION_SCHEMA.COLUMNS 
  WHERE TABLE_SCHEMA = 'bookstore' 
  AND TABLE_NAME = 'spikerecord' 
  AND COLUMN_NAME = 'quantity'
);

SET @sql = IF(@column_exists = 0, 
  'ALTER TABLE spikerecord ADD COLUMN quantity int NOT NULL DEFAULT 1 COMMENT ''购买数量'' AFTER userAccount;',
  'SELECT ''quantity column already exists'' as message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ========================================
-- 5. 创建性能优化索引
-- ========================================

-- 秒杀活动表复合索引
CREATE INDEX `idx_activity_time_status` ON `spikeactivity` (`startTime`, `endTime`, `status`);

-- 秒杀商品表复合索引  
CREATE INDEX `idx_goods_activity_status` ON `spikegoods` (`activityId`, `status`);
CREATE INDEX `idx_goods_book_status` ON `spikegoods` (`bookId`, `status`);

-- 秒杀记录表复合索引（用于限购检查优化）
CREATE INDEX `idx_record_user_goods_result` ON `spikerecord` (`userAccount`, `spikeGoodsId`, `result`);

-- ========================================
-- 6. 插入示例数据（仅在表为空时插入）
-- ========================================

-- 插入秒杀活动示例数据
INSERT IGNORE INTO `spikeactivity` (`id`, `activityName`, `activityDesc`, `startTime`, `endTime`, `status`, `createdBy`) VALUES
(1, '新年特惠秒杀', '新年期间限时秒杀活动，精选图书超低价', '2024-01-01 10:00:00', '2024-01-01 12:00:00', 1, 'admin@bookstore.com'),
(2, '午间秒杀场', '午间休息时间秒杀专场', '2024-01-01 12:00:00', '2024-01-01 14:00:00', 0, 'admin@bookstore.com'),
(3, '晚间秒杀场', '晚间黄金时段秒杀活动', '2024-01-01 20:00:00', '2024-01-01 22:00:00', 0, 'admin@bookstore.com');

-- 插入秒杀商品示例数据（基于现有的图书数据）
INSERT IGNORE INTO `spikegoods` (`id`, `activityId`, `bookId`, `spikePrice`, `originalPrice`, `spikeStock`, `limitPerUser`, `sortOrder`) VALUES
(1, 1, 1, 19.90, 45.00, 50, 2, 1),  -- 红楼梦
(2, 1, 2, 49.90, 98.00, 30, 1, 2),  -- Java核心技术
(3, 1, 3, 69.90, 128.00, 20, 1, 3), -- 算法导论
(4, 2, 4, 39.90, 69.00, 40, 2, 1),  -- Spring Boot实战
(5, 2, 5, 15.90, 38.00, 60, 2, 2),  -- 西游记
(6, 3, 1, 22.90, 45.00, 30, 1, 1),  -- 红楼梦（晚场）
(7, 3, 2, 59.90, 98.00, 25, 1, 2);  -- Java核心技术（晚场）

-- ========================================
-- 7. 清理工作
-- ========================================

-- 删除旧的spikeorder备份表（数据已迁移到spikerecord表）
DROP TABLE IF EXISTS `spikeorder_backup`;

-- ========================================
-- 8. 验证安装
-- ========================================

-- 显示创建的表
SELECT 'Tables created successfully:' as message;
SHOW TABLES LIKE '%spike%';

-- 显示表记录数
SELECT 
  'spikeactivity' as table_name, 
  COUNT(*) as record_count 
FROM spikeactivity
UNION ALL
SELECT 
  'spikegoods' as table_name, 
  COUNT(*) as record_count 
FROM spikegoods
UNION ALL
SELECT 
  'spikerecord' as table_name, 
  COUNT(*) as record_count 
FROM spikerecord;

-- ========================================
-- 安装完成提示
-- ========================================
SELECT '========================================' as message
UNION ALL
SELECT '秒杀系统数据库更新完成！' as message
UNION ALL
SELECT '版本：v1.0' as message
UNION ALL
SELECT '更新内容：' as message
UNION ALL
SELECT '1. 创建/更新秒杀活动表(spikeactivity)' as message
UNION ALL
SELECT '2. 创建/更新秒杀商品表(spikegoods)' as message
UNION ALL
SELECT '3. 创建/更新秒杀记录表(spikerecord)' as message
UNION ALL
SELECT '4. 添加quantity字段用于限购检查' as message
UNION ALL
SELECT '5. 优化索引提升查询性能' as message
UNION ALL
SELECT '6. 插入示例数据供测试使用' as message
UNION ALL
SELECT '========================================' as message;
