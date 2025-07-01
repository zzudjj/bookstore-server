# 秒杀系统数据库更新指南

## 📋 概述

本次更新为现有的bookstore系统添加了完整的秒杀功能，包括秒杀活动管理、商品管理、用户限购等核心功能。

## 🗃️ 涉及的数据表

### 新增表：
1. **spikeactivity** - 秒杀活动表
2. **spikegoods** - 秒杀商品表  
3. **spikerecord** - 秒杀记录表（用于限购检查和行为分析）

### 修改表：
- **spikerecord** - 添加了 `quantity` 字段用于限购功能

### 删除表：
- **spikeorder** - 已删除，改用统一订单系统

## 🚀 安装步骤

### 1. 备份数据库（重要！）
```bash
mysqldump -u root -p bookstore > bookstore_backup_$(date +%Y%m%d_%H%M%S).sql
```

### 2. 执行更新脚本
```bash
mysql -u root -p bookstore < spike_system_update.sql
```

或者在MySQL客户端中：
```sql
USE bookstore;
SOURCE /path/to/spike_system_update.sql;
```

### 3. 验证安装
脚本执行完成后会显示：
- 创建的表列表
- 各表的记录数量
- 安装完成提示

## 📊 数据表结构说明

### spikeactivity（秒杀活动表）
- 管理秒杀活动的基本信息
- 包含活动时间、状态等字段
- 支持活动的创建、编辑、启停

### spikegoods（秒杀商品表）
- 管理参与秒杀的商品信息
- 关联图书表(book)和活动表(spikeactivity)
- 包含秒杀价格、库存、限购数量等

### spikerecord（秒杀记录表）
- 记录所有秒杀行为（成功/失败）
- **新增quantity字段**：用于限购检查
- 包含IP地址、用户代理等详细信息

## 🔧 核心功能

### 1. 限购检查
通过spikerecord表统计用户成功购买数量：
```sql
SELECT COALESCE(SUM(quantity), 0) 
FROM spikerecord 
WHERE userAccount = ? AND spikeGoodsId = ? AND result = 1
```

### 2. 库存管理
- 秒杀库存独立管理
- 与图书表库存同步扣减
- 支持库存回滚机制

### 3. 统一订单系统
- 秒杀订单使用现有订单系统
- 避免重复的订单管理逻辑
- 用户体验统一

## ⚠️ 注意事项

### 1. 兼容性
- 脚本使用 `CREATE TABLE IF NOT EXISTS` 确保安全执行
- 自动检查并添加缺失的字段
- 不会影响现有数据

### 2. 性能优化
- 添加了复合索引提升查询性能
- 限购检查查询已优化
- 支持高并发秒杀场景

### 3. 数据安全
- 保留了外键约束确保数据完整性
- 支持级联删除避免孤立数据
- 建议定期备份数据

## 🧪 测试数据

脚本会自动插入示例数据：
- 3个秒杀活动（新年特惠、午间场、晚间场）
- 7个秒杀商品（基于现有图书）
- 可用于功能测试和演示

## 📞 技术支持

如果在安装过程中遇到问题：

1. **检查MySQL版本**：建议使用MySQL 8.0+
2. **检查权限**：确保用户有CREATE、ALTER、INSERT权限
3. **检查依赖**：确保book表和user表存在
4. **查看日志**：检查MySQL错误日志

## 🔄 回滚方案

如需回滚更新：
```sql
-- 删除秒杀相关表
DROP TABLE IF EXISTS spikerecord;
DROP TABLE IF EXISTS spikegoods; 
DROP TABLE IF EXISTS spikeactivity;

-- 恢复备份（如果需要）
-- mysql -u root -p bookstore < bookstore_backup_YYYYMMDD_HHMMSS.sql
```

## 📈 版本信息

- **版本**：v1.0
- **发布日期**：2024-01-01
- **兼容性**：bookstore系统 v1.0+
- **MySQL版本**：5.7+ / 8.0+
