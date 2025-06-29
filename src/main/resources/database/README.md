# 书店系统数据库脚本

本目录包含了书店系统的完整数据库初始化脚本，包括表结构创建和示例数据。

## 文件说明

### 1. `bookstore_ddl.sql`
- **用途**: 数据库表结构创建脚本
- **内容**: 包含所有表的创建语句、索引和外键约束
- **执行顺序**: 第一个执行

### 2. `bookstore_sample_data.sql`
- **用途**: 示例数据插入脚本
- **内容**: 包含测试用的用户、图书、订单等数据
- **执行顺序**: 在DDL脚本执行完成后执行

### 3. `README.md`
- **用途**: 使用说明文档

## 数据库设计概览

### 核心表结构

#### 用户相关表
- `user` - 用户基本信息
- `address` - 收货地址
- `cart` - 购物车

#### 图书相关表
- `book` - 图书基本信息
- `bookimg` - 图书图片
- `booksort` - 图书分类
- `booksortlist` - 图书分类关联
- `publish` - 出版社

#### 书单相关表
- `booktopic` - 书单主题
- `subbooktopic` - 书单图书关联

#### 订单相关表
- `bookorder` - 订单主表
- `orderdetail` - 订单明细
- `expense` - 订单费用

## 使用方法

### 1. 环境要求
- MySQL 5.7+ 或 MySQL 8.0+
- 字符集支持: utf8mb4

### 2. 执行步骤

#### 方法一：使用MySQL命令行
```bash
# 1. 登录MySQL
mysql -u root -p

# 2. 执行DDL脚本创建表结构
source src/main/resources/database/bookstore_ddl.sql

# 3. 执行示例数据脚本
source src/main/resources/database/bookstore_sample_data.sql
```

#### 方法二：使用MySQL Workbench
1. 打开MySQL Workbench
2. 连接到MySQL服务器
3. 打开并执行 `src/main/resources/database/bookstore_ddl.sql`
4. 打开并执行 `src/main/resources/database/bookstore_sample_data.sql`

#### 方法三：使用命令行直接执行
```bash
# 创建数据库和表结构
mysql -u root -p < src/main/resources/database/bookstore_ddl.sql

# 插入示例数据
mysql -u root -p < src/main/resources/database/bookstore_sample_data.sql
```

### 3. 验证安装

执行以下SQL语句验证数据库是否正确创建：

```sql
-- 检查数据库
SHOW DATABASES LIKE 'bookstore';

-- 检查表
USE bookstore;
SHOW TABLES;

-- 检查数据
SELECT COUNT(*) as user_count FROM user;
SELECT COUNT(*) as book_count FROM book;
SELECT COUNT(*) as order_count FROM bookorder;
```

## 配置说明

### 数据库连接配置

根据您的MySQL配置，请在Spring Boot应用的配置文件中设置正确的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bookstore?characterEncoding=utf-8
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 默认用户账号

示例数据中包含以下测试账号：

| 账号 | 密码 | 角色 | 说明 |
|------|------|------|------|
| admin@bookstore.com | 123456 | 管理员 | 系统管理员账号 |
| user1@example.com | 123456 | 普通用户 | 测试用户1 |
| user2@example.com | 123456 | 普通用户 | 测试用户2 |

**注意**: 密码已使用BCrypt加密，原始密码为 `123456`

## 注意事项

1. **字符集**: 确保MySQL服务器支持utf8mb4字符集
2. **权限**: 执行脚本的用户需要有创建数据库和表的权限
3. **备份**: 在生产环境中执行前请做好数据备份
4. **外键约束**: 脚本包含外键约束，删除数据时请注意依赖关系
5. **索引优化**: 根据实际使用情况可能需要调整索引策略

## 故障排除

### 常见问题

1. **字符集问题**
   ```sql
   -- 检查数据库字符集
   SHOW CREATE DATABASE bookstore;
   
   -- 如果字符集不正确，重新创建数据库
   DROP DATABASE IF EXISTS bookstore;
   CREATE DATABASE bookstore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **外键约束错误**
   - 确保按照正确顺序执行脚本
   - 检查是否有数据不一致的情况

3. **权限不足**
   ```sql
   -- 授予用户权限
   GRANT ALL PRIVILEGES ON bookstore.* TO 'username'@'localhost';
   FLUSH PRIVILEGES;
   ```

## 扩展说明

如果需要修改表结构或添加新功能，建议：

1. 创建迁移脚本而不是直接修改DDL文件
2. 保持向后兼容性
3. 更新相应的实体类和Mapper文件
4. 添加必要的测试数据

## 项目集成

### 在Spring Boot项目中使用

1. **自动执行脚本**（可选）
   
   可以在 `application.yml` 中配置自动执行SQL脚本：
   ```yaml
   spring:
     sql:
       init:
         mode: always
         schema-locations: classpath:database/bookstore_ddl.sql
         data-locations: classpath:database/bookstore_sample_data.sql
   ```

2. **手动执行**（推荐）
   
   建议手动执行脚本，这样可以更好地控制数据库初始化过程。

### 开发环境配置

确保您的 `application-dev.yml` 中的数据库配置与脚本创建的数据库匹配：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bookstore?characterEncoding=utf-8
    username: root
    password: root
```

## 联系方式

如有问题，请通过以下方式联系：
- 项目Issues
- 技术支持邮箱
