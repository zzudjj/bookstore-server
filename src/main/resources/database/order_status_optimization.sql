-- ========================================
-- 库存管理与订单超时处理优化脚本
-- 版本: v2.0
-- 创建时间: 2024-01-01
-- 更新时间: 2025-07-02
-- 描述: 实现库存预留机制，移除不必要的表和字段
-- ========================================

-- 1. 备份当前订单状态分布
SELECT '执行前订单状态分布:' as message;
SELECT orderStatus, COUNT(*) as count
FROM bookorder
GROUP BY orderStatus
ORDER BY count DESC;

-- 2. 统一订单状态
UPDATE bookorder SET orderStatus = '已付款' WHERE orderStatus IN ('待发货');
UPDATE bookorder SET orderStatus = '已完成' WHERE orderStatus IN ('已收货', '已评价');

-- 3. 添加新字段（检查字段是否存在）
-- 添加支付超时字段
SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'bookstore' AND TABLE_NAME = 'bookorder' AND COLUMN_NAME = 'paymentDeadline') = 0,
    'ALTER TABLE bookorder ADD COLUMN paymentDeadline TIMESTAMP NULL COMMENT ''支付截止时间''',
    'SELECT ''paymentDeadline字段已存在'' as message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加取消原因字段
SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'bookstore' AND TABLE_NAME = 'bookorder' AND COLUMN_NAME = 'cancelReason') = 0,
    'ALTER TABLE bookorder ADD COLUMN cancelReason VARCHAR(255) NULL COMMENT ''取消原因''',
    'SELECT ''cancelReason字段已存在'' as message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加操作人字段
SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'bookstore' AND TABLE_NAME = 'bookorder' AND COLUMN_NAME = 'operator') = 0,
    'ALTER TABLE bookorder ADD COLUMN operator VARCHAR(50) NULL COMMENT ''最后操作人''',
    'SELECT ''operator字段已存在'' as message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 4. 删除不使用的订单状态变更日志表
DROP TABLE IF EXISTS order_status_log;

-- 5. 创建库存预留表
CREATE TABLE IF NOT EXISTS stock_reservation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL COMMENT '图书ID',
    order_id VARCHAR(50) NOT NULL COMMENT '订单ID',
    reserved_quantity INT NOT NULL COMMENT '预留数量',
    expire_time TIMESTAMP NOT NULL COMMENT '过期时间',
    status TINYINT DEFAULT 0 COMMENT '状态：0-预留中，1-已确认，2-已释放',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_book_id (book_id),
    INDEX idx_order_id (order_id),
    INDEX idx_expire_time (expire_time),
    INDEX idx_status (status)
) COMMENT '库存预留表';

-- 6. 创建订单配置表
CREATE TABLE IF NOT EXISTS order_config (
    id INT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(50) NOT NULL UNIQUE COMMENT '配置键',
    config_value VARCHAR(255) NOT NULL COMMENT '配置值',
    description VARCHAR(255) COMMENT '配置描述',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_config_key (config_key)
) COMMENT '订单配置表';

-- 插入默认配置
INSERT INTO order_config (config_key, config_value, description) VALUES
('spike_payment_timeout', '30', '秒杀订单付款超时时间（分钟）'),
('normal_payment_timeout', '60', '正常订单付款超时时间（分钟）'),
('delivery_timeout', '7', '发货超时时间（天）'),
('receive_timeout', '7', '收货超时时间（天）')
ON DUPLICATE KEY UPDATE
config_value = VALUES(config_value),
description = VALUES(description);

-- 7. 创建订单统计视图
CREATE OR REPLACE VIEW order_status_summary AS
SELECT
    orderStatus,
    COUNT(*) as total_count,
    SUM(CASE WHEN DATE(orderTime) = CURDATE() THEN 1 ELSE 0 END) as today_count,
    SUM(CASE WHEN DATE(orderTime) >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) THEN 1 ELSE 0 END) as week_count,
    AVG(CASE WHEN orderStatus = '已完成' THEN
        TIMESTAMPDIFF(HOUR, orderTime, confirmTime)
        ELSE NULL END) as avg_completion_hours
FROM bookorder
WHERE beUserDelete = 0
GROUP BY orderStatus;

-- 8. 添加基础索引优化（如果不存在）
-- 订单状态索引
CREATE INDEX idx_order_status ON bookorder(orderStatus);

-- 订单时间索引
CREATE INDEX idx_order_time ON bookorder(orderTime);

-- 用户账号和状态复合索引
CREATE INDEX idx_account_status ON bookorder(account, orderStatus);

-- 9. 验证优化结果
SELECT '执行后订单状态分布:' as message;
SELECT orderStatus, COUNT(*) as count
FROM bookorder
GROUP BY orderStatus
ORDER BY count DESC;

-- 显示新增表信息
SELECT '新增表信息:' as message;
SHOW TABLES LIKE '%stock_reservation%';

-- 显示新增字段信息
SELECT '新增字段信息:' as message;
DESCRIBE bookorder;

-- 显示索引信息
SELECT '索引信息:' as message;
SHOW INDEX FROM bookorder WHERE Key_name LIKE 'idx_%';

SELECT '订单状态优化完成! (已移除状态日志表)' as message;
