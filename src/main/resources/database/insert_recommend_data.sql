-- 插入推荐图书数据
-- 这个脚本用于为推荐图书区域添加测试数据

USE `bookstore`;

-- 首先检查是否有图书数据
SELECT COUNT(*) as book_count FROM book;

-- 清空现有的推荐数据（如果需要重新开始）
-- DELETE FROM recommend;
-- DELETE FROM newproduct;

-- 插入推荐图书数据
-- 选择前10本已上架的图书作为推荐图书
INSERT IGNORE INTO `recommend` (`bookId`, `rank`, `addTime`)
SELECT `id`, 1, NOW() FROM `book` 
WHERE `put` = 1 
ORDER BY `id` 
LIMIT 10;

-- 插入新品推荐数据
-- 选择前10本已上架的图书作为新品推荐
INSERT IGNORE INTO `newproduct` (`bookId`, `rank`, `addTime`)
SELECT `id`, 1, NOW() FROM `book` 
WHERE `put` = 1 
ORDER BY `id` DESC
LIMIT 10;

-- 更新book表中的推荐和新品标志
UPDATE `book` SET `recommend` = 1 
WHERE `id` IN (SELECT `bookId` FROM `recommend`);

UPDATE `book` SET `newProduct` = 1 
WHERE `id` IN (SELECT `bookId` FROM `newproduct`);

-- 查看插入结果
SELECT 'recommend表数据:' as info;
SELECT r.id, r.bookId, b.bookName, r.rank, r.addTime 
FROM recommend r 
JOIN book b ON r.bookId = b.id 
ORDER BY r.rank DESC, r.addTime DESC;

SELECT 'newproduct表数据:' as info;
SELECT n.id, n.bookId, b.bookName, n.rank, n.addTime 
FROM newproduct n 
JOIN book b ON n.bookId = b.id 
ORDER BY n.rank DESC, n.addTime DESC;

-- 检查book表中的推荐标志
SELECT 'book表中的推荐图书:' as info;
SELECT id, bookName, author, recommend, newProduct, put 
FROM book 
WHERE recommend = 1 OR newProduct = 1
ORDER BY id;
