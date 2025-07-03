-- ========================================
-- 书店系统优惠券扩展SQL脚本
-- 支持满减券和折扣券两种类型
-- 管理员可创建发放，用户可领取使用
--
-- 使用说明：
-- 1. 此脚本适用于已有bookstore数据库的系统
-- 2. 会在现有系统基础上添加优惠券功能
-- 3. 如果表已存在会报错，这是正常的，可以忽略
-- ========================================

-- 1. 优惠券模板表（管理员创建的优惠券类型）
CREATE TABLE `coupon_template` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '优惠券模板ID',
    `name` varchar(100) NOT NULL COMMENT '优惠券名称',
    `type` tinyint(1) NOT NULL COMMENT '优惠券类型：1-满减券，2-折扣券',
    `discount_value` decimal(10,2) NOT NULL COMMENT '折扣值（满减券为减免金额，折扣券为折扣百分比，如85表示8.5折）',
    `min_order_amount` decimal(10,2) DEFAULT '0.00' COMMENT '最低消费金额',
    `max_discount_amount` decimal(10,2) DEFAULT NULL COMMENT '最大折扣金额（仅折扣券使用）',
    `total_quantity` int(11) NOT NULL COMMENT '发放总数量',
    `used_quantity` int(11) DEFAULT '0' COMMENT '已使用数量',
    `received_quantity` int(11) DEFAULT '0' COMMENT '已领取数量',
    `per_user_limit` int(11) DEFAULT '1' COMMENT '每用户限领数量',
    `valid_days` int(11) NOT NULL COMMENT '有效天数（从领取日开始计算）',
    `status` tinyint(1) DEFAULT '1' COMMENT '状态：0-停用，1-启用',
    `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券模板表';

-- 2. 用户优惠券表（用户领取的优惠券实例）
CREATE TABLE `user_coupon` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户优惠券ID',
    `coupon_template_id` int(11) NOT NULL COMMENT '优惠券模板ID',
    `account` varchar(100) NOT NULL COMMENT '用户账号',
    `coupon_code` varchar(50) NOT NULL COMMENT '优惠券码（自动生成）',
    `status` tinyint(2) DEFAULT '1' COMMENT '状态：1-未使用，2-已使用，3-已过期',
    `receive_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
    `use_time` timestamp NULL DEFAULT NULL COMMENT '使用时间',
    `order_id` varchar(50) DEFAULT NULL COMMENT '使用的订单号',
    `expire_time` timestamp NOT NULL COMMENT '过期时间',
    `discount_amount` decimal(10,2) DEFAULT '0.00' COMMENT '实际折扣金额（使用时计算并存储）',
    `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_coupon_code` (`coupon_code`),
    KEY `idx_account` (`account`),
    KEY `idx_template_id` (`coupon_template_id`),
    KEY `idx_status` (`status`),
    KEY `idx_expire_time` (`expire_time`),
    KEY `idx_account_status` (`account`, `status`),
    CONSTRAINT `fk_user_coupon_template` FOREIGN KEY (`coupon_template_id`) REFERENCES `coupon_template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';

-- ========================================
-- 修改现有表结构
-- ========================================

-- 3. 修改expense表，增加优惠券相关字段
ALTER TABLE `expense`
ADD COLUMN `coupon_id` int(11) DEFAULT NULL COMMENT '使用的优惠券ID' AFTER `coupon`,
ADD COLUMN `coupon_discount` decimal(10,2) DEFAULT '0.00' COMMENT '优惠券折扣金额' AFTER `coupon_id`,
ADD INDEX `idx_coupon_id` (`coupon_id`);

-- 4. 修改bookorder表，增加优惠券字段
ALTER TABLE `bookorder`
ADD COLUMN `coupon_id` int(11) DEFAULT NULL COMMENT '使用的优惠券ID' AFTER `beUserDelete`;

-- ========================================
-- 插入示例数据
-- ========================================

-- 插入优惠券模板示例数据
INSERT INTO `coupon_template` (`name`, `type`, `discount_value`, `min_order_amount`, `max_discount_amount`, `total_quantity`, `per_user_limit`, `valid_days`) VALUES
('新用户专享券', 1, 20.00, 100.00, NULL, 1000, 1, 30),
('满200减50券', 1, 50.00, 200.00, NULL, 500, 2, 15),
('图书节8.5折券', 2, 85.00, 50.00, 50.00, 200, 1, 7),
('会员9折券', 2, 90.00, 100.00, 30.00, 300, 3, 30);

-- 插入用户优惠券示例数据（为已有用户分配优惠券）
INSERT INTO `user_coupon` (`coupon_template_id`, `account`, `coupon_code`, `expire_time`) VALUES
(2, 'admin@bookstore.com', 'FULL20241201002000001', DATE_ADD(NOW(), INTERVAL 15 DAY)),
(1, 'user1@example.com', 'FULL20241201001000002', DATE_ADD(NOW(), INTERVAL 30 DAY)),
(3, 'user1@example.com', 'DISC20241201003000001', DATE_ADD(NOW(), INTERVAL 7 DAY));

-- ========================================
-- 创建视图（方便查询）
-- ========================================

-- 创建用户可用优惠券视图
CREATE VIEW `user_available_coupons` AS
SELECT
    uc.id as user_coupon_id,
    uc.account,
    uc.coupon_code,
    ct.name as coupon_name,
    ct.type,
    ct.discount_value,
    ct.min_order_amount,
    ct.max_discount_amount,
    uc.receive_time,
    uc.expire_time,
    CASE
        WHEN ct.type = 1 THEN CONCAT('满', ct.min_order_amount, '减', ct.discount_value)
        WHEN ct.type = 2 THEN CONCAT(ct.discount_value/10, '折优惠')
    END as discount_desc
FROM user_coupon uc
JOIN coupon_template ct ON uc.coupon_template_id = ct.id
WHERE uc.status = 1
  AND uc.expire_time > NOW()
  AND ct.status = 1;

-- ========================================
-- 使用说明
-- ========================================

/*
优惠券系统使用流程：

1. 管理员创建优惠券模板（coupon_template表）
   - 设置优惠券类型（1-满减，2-折扣）
   - 设置折扣值和使用条件
   - 设置发放数量和有效期

2. 用户领取优惠券
   - 系统自动生成优惠券码
   - 计算过期时间（领取时间 + 有效天数）
   - 更新模板的已领取数量

3. 用户使用优惠券
   - 下单时选择可用优惠券
   - 系统验证优惠券有效性
   - 计算折扣金额并更新订单

4. 折扣计算逻辑：
   满减券：订单金额 >= min_order_amount 时，减免 discount_value
   折扣券：订单金额 * (discount_value/100)，但不超过 max_discount_amount

优惠券类型说明：
- type=1: 满减券（满X元减Y元）
  - discount_value: 减免金额
  - min_order_amount: 最低消费金额

- type=2: 折扣券（X折优惠）
  - discount_value: 折扣百分比（如85表示8.5折）
  - min_order_amount: 最低消费金额
  - max_discount_amount: 最大折扣金额

优惠券码格式：
- 满减券：FULL + 日期 + 模板ID + 随机数
- 折扣券：DISC + 日期 + 模板ID + 随机数
*/
