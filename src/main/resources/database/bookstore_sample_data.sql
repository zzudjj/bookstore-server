-- ========================================
-- 书店系统示例数据插入脚本
-- 注意：请先执行 bookstore_ddl.sql 创建表结构
-- ========================================

USE `bookstore`;

-- ========================================
-- 用户数据
-- ========================================

-- 插入管理员用户
INSERT INTO `user` (`account`, `password`, `name`, `gender`, `manage`, `enable`) VALUES
('admin@bookstore.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '管理员', '男', 1, 1),
('user1@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '张三', '男', 0, 1),
('user2@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '李四', '女', 0, 1);

-- 插入地址数据
INSERT INTO `address` (`account`, `name`, `phone`, `addr`, `label`) VALUES
('user1@example.com', '张三', '13800138001', '北京市朝阳区某某街道123号', '家'),
('user1@example.com', '张三', '13800138001', '北京市海淀区某某大厦456号', '公司'),
('user2@example.com', '李四', '13800138002', '上海市浦东新区某某路789号', '家');

-- ========================================
-- 图书分类数据
-- ========================================

-- 一级分类
INSERT INTO `booksort` (`sortName`, `upperName`, `level`, `rank`) VALUES
('文学', '无', '级别一', 1),
('科技', '无', '级别一', 2),
('教育', '无', '级别一', 3),
('生活', '无', '级别一', 4);

-- 二级分类
INSERT INTO `booksort` (`sortName`, `upperName`, `level`, `rank`) VALUES
('小说', '文学', '级别二', 1),
('散文', '文学', '级别二', 2),
('诗歌', '文学', '级别二', 3),
('计算机', '科技', '级别二', 1),
('电子', '科技', '级别二', 2),
('教材', '教育', '级别二', 1),
('考试', '教育', '级别二', 2),
('健康', '生活', '级别二', 1),
('美食', '生活', '级别二', 2);

-- ========================================
-- 出版社数据
-- ========================================

INSERT INTO `publish` (`name`, `showPublish`, `rank`) VALUES
('人民文学出版社', 1, 1),
('机械工业出版社', 1, 2),
('清华大学出版社', 1, 3),
('电子工业出版社', 1, 4),
('中信出版社', 1, 5);

-- ========================================
-- 图书数据
-- ========================================

INSERT INTO `book` (`bookName`, `author`, `isbn`, `publish`, `birthday`, `marketPrice`, `price`, `stock`, `description`, `put`, `rank`, `newProduct`, `recommend`) VALUES
('红楼梦', '曹雪芹', '9787020002207', '人民文学出版社', '2020-01-01 00:00:00', 59.00, 45.00, 100, '中国古典文学四大名著之一', 1, 10, 0, 1),
('Java核心技术', 'Cay S. Horstmann', '9787111213826', '机械工业出版社', '2021-03-15 00:00:00', 128.00, 98.00, 50, 'Java编程经典教程', 1, 9, 1, 1),
('算法导论', 'Thomas H. Cormen', '9787111407010', '机械工业出版社', '2020-06-01 00:00:00', 158.00, 128.00, 30, '计算机算法经典教材', 1, 8, 0, 1),
('Spring Boot实战', '汪云飞', '9787121291005', '电子工业出版社', '2021-05-20 00:00:00', 89.00, 69.00, 80, 'Spring Boot开发实战指南', 1, 7, 1, 0),
('西游记', '吴承恩', '9787020002214', '人民文学出版社', '2020-02-01 00:00:00', 49.00, 38.00, 120, '中国古典文学四大名著之一', 1, 6, 0, 1);

-- ========================================
-- 图书图片数据
-- ========================================

INSERT INTO `bookimg` (`isbn`, `imgSrc`, `cover`) VALUES
('9787020002207', 'static/image/book/hongloumeng_cover.jpg', 1),
('9787020002207', 'static/image/book/hongloumeng_1.jpg', 0),
('9787111213826', 'static/image/book/java_cover.jpg', 1),
('9787111213826', 'static/image/book/java_1.jpg', 0),
('9787111407010', 'static/image/book/algorithm_cover.jpg', 1),
('9787121291005', 'static/image/book/springboot_cover.jpg', 1),
('9787020002214', 'static/image/book/xiyouji_cover.jpg', 1);

-- ========================================
-- 图书分类关联数据
-- ========================================

INSERT INTO `booksortlist` (`bookSortId`, `bookId`) VALUES
(5, 1),  -- 红楼梦 -> 小说
(5, 5),  -- 西游记 -> 小说
(8, 2),  -- Java核心技术 -> 计算机
(8, 3),  -- 算法导论 -> 计算机
(8, 4);  -- Spring Boot实战 -> 计算机

-- ========================================
-- 书单数据
-- ========================================

INSERT INTO `booktopic` (`topicName`, `subTitle`, `cover`, `rank`, `put`) VALUES
('程序员必读书单', '提升编程技能的经典书籍', 'static/image/topic/programmer_books.jpg', 1, 1),
('古典文学精选', '传承千年的文学瑰宝', 'static/image/topic/classic_literature.jpg', 2, 1);

INSERT INTO `subbooktopic` (`topicId`, `bookId`, `recommendReason`) VALUES
(1, 2, 'Java开发者的必备参考书，内容全面深入'),
(1, 3, '算法学习的经典教材，计算机科学基础'),
(1, 4, '现代Java开发框架实战指南'),
(2, 1, '中国古典小说的巅峰之作，文学价值极高'),
(2, 5, '神话色彩浓厚的古典小说，想象力丰富');

-- ========================================
-- 购物车示例数据
-- ========================================

INSERT INTO `cart` (`account`, `id`, `num`) VALUES
('user1@example.com', 1, 2),
('user1@example.com', 2, 1),
('user2@example.com', 3, 1);

-- ========================================
-- 订单示例数据
-- ========================================

INSERT INTO `bookorder` (`orderId`, `account`, `addressId`, `orderTime`, `orderStatus`) VALUES
('ORD202401010001', 'user1@example.com', 1, '2024-01-01 10:30:00', '已完成'),
('ORD202401020001', 'user2@example.com', 3, '2024-01-02 14:20:00', '待发货');

INSERT INTO `orderdetail` (`orderId`, `bookId`, `num`, `price`) VALUES
('ORD202401010001', 1, 1, 45.00),
('ORD202401010001', 2, 1, 98.00),
('ORD202401020001', 3, 1, 128.00);

INSERT INTO `expense` (`orderId`, `productTotalMoney`, `freight`, `coupon`, `activityDiscount`, `allPrice`, `finallyPrice`) VALUES
('ORD202401010001', 143.00, 0.00, 0, 0.00, 143.00, 143.00),
('ORD202401020001', 128.00, 0.00, 0, 0.00, 128.00, 128.00);

-- ========================================
-- 更新出版社图书数量
-- ========================================

UPDATE `publish` SET `num` = (
    SELECT COUNT(*) FROM `book` WHERE `book`.`publish` = `publish`.`name`
) WHERE `id` > 0;

-- ========================================
-- 公告示例数据
-- ========================================

INSERT INTO `announcement` (`title`, `content`, `author`, `publishTime`, `enable`) VALUES
('系统维护通知', '尊敬的读者：网站将于本周日 00:00-02:00 进行服务器维护，期间将暂停访问，敬请谅解。', 'admin@bookstore.com', NOW(), 1),
('新书上架公告', '本月新上架 50 余种精品好书，欢迎大家选购！', 'admin@bookstore.com', NOW(), 1);

-- ========================================
-- 网站介绍示例数据（仅 1 条）
-- ========================================

INSERT INTO `about` (`id`, `content`, `updateTime`) VALUES
(1, '智慧书店致力于为读者提供多元化、精品化的阅读体验——精选图书、每日特价、秒杀活动，尽享阅读乐趣。', NOW());
