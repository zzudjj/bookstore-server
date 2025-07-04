-- ========================================
-- 图书评价表
-- ========================================
CREATE TABLE `book_comment` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '评论编号',
    `bookId` int(11) NOT NULL COMMENT '图书ID',
    `userId` int(11) NOT NULL COMMENT '用户ID',
    `parentId` int(11) DEFAULT NULL COMMENT '父评论ID（用于二级评论）',
    `content` text NOT NULL COMMENT '评论内容',
    `likeCount` int(11) DEFAULT '0' COMMENT '点赞数',
    `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_bookId` (`bookId`),
    KEY `idx_userId` (`userId`),
    KEY `idx_parentId` (`parentId`),
    KEY `idx_createTime` (`createTime`),
    KEY `idx_likeCount` (`likeCount`),
    CONSTRAINT `fk_comment_book` FOREIGN KEY (`bookId`) REFERENCES `book` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_comment_user` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parentId`) REFERENCES `book_comment` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书评价表';

-- ========================================
-- 评论点赞记录表（可选，用于记录用户点赞历史）
-- ========================================
CREATE TABLE `comment_like` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '点赞记录编号',
    `commentId` int(11) NOT NULL COMMENT '评论ID',
    `userId` int(11) NOT NULL COMMENT '用户ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_comment_user` (`commentId`, `userId`),
    KEY `idx_userId` (`userId`),
    CONSTRAINT `fk_like_comment` FOREIGN KEY (`commentId`) REFERENCES `book_comment` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_like_user` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论点赞记录表';

-- ========================================
-- 插入示例数据
-- ========================================

-- 插入图书评论示例数据（一级评论）
INSERT INTO `book_comment` (`bookId`, `userId`, `content`, `likeCount`, `createTime`) VALUES
(1, 2, '这本书写得非常好，内容详实，值得推荐！', 15, '2024-01-01 10:30:00'),
(1, 3, '红楼梦不愧是四大名著之一，文笔优美，情节引人入胜。', 23, '2024-01-02 14:20:00'),
(2, 2, 'Java核心技术这本书对初学者很友好，讲解得很清楚。', 18, '2024-01-03 09:15:00'),
(2, 3, '作为Java开发者，这本书是必读的经典教材。', 31, '2024-01-04 16:45:00'),
(3, 2, '算法导论内容很全面，但有些章节比较难理解。', 12, '2024-01-05 11:30:00'),
(3, 3, '这本书是算法学习的权威教材，值得反复阅读。', 27, '2024-01-06 13:20:00'),
(4, 2, 'Spring Boot实战很实用，案例丰富，适合项目开发。', 19, '2024-01-07 15:10:00'),
(5, 3, '西游记的故事很有趣，适合各个年龄段的读者。', 25, '2024-01-08 10:25:00');

-- 插入二级评论示例数据（回复评论）
INSERT INTO `book_comment` (`bookId`, `userId`, `parentId`, `content`, `likeCount`, `createTime`) VALUES
(1, 3, 1, '同意你的观点，这本书确实很棒！', 8, '2024-01-01 11:00:00'),
(1, 2, 1, '谢谢推荐，我也觉得很有收获。', 5, '2024-01-01 12:30:00'),
(1, 2, 2, '曹雪芹的文笔确实很厉害，每个角色都刻画得很生动。', 12, '2024-01-02 15:00:00'),
(2, 3, 3, '对，这本书很适合入门学习。', 6, '2024-01-03 10:00:00'),
(2, 2, 4, '确实，这本书在Java圈子里口碑很好。', 9, '2024-01-04 17:00:00'),
(3, 3, 5, '建议先看基础章节，再深入复杂算法。', 7, '2024-01-05 12:00:00'),
(3, 2, 6, '同意，经典教材值得收藏。', 4, '2024-01-06 14:00:00'),
(4, 3, 7, 'Spring Boot确实让开发变得更简单了。', 11, '2024-01-07 16:00:00'),
(5, 2, 8, '西游记的想象力真的很丰富！', 8, '2024-01-08 11:00:00');

-- 插入评论点赞记录示例数据
INSERT INTO `comment_like` (`commentId`, `userId`) VALUES
(1, 3),
(1, 2),
(2, 2),
(2, 3),
(3, 3),
(3, 2),
(4, 2),
(4, 3),
(5, 3),
(6, 2),
(7, 3),
(8, 2),
(9, 2),
(10, 3),
(11, 2),
(12, 3),
(13, 2),
(14, 3),
(15, 2),
(16, 3),
(17, 2);

-- ========================================
-- 验证数据插入结果
-- ========================================

-- 查看评论数据统计
SELECT 
    'book_comment' as table_name,
    COUNT(*) as total_comments,
    COUNT(CASE WHEN parentId IS NULL THEN 1 END) as first_level_comments,
    COUNT(CASE WHEN parentId IS NOT NULL THEN 1 END) as second_level_comments,
    SUM(likeCount) as total_likes
FROM book_comment;

-- 查看点赞记录统计
SELECT 
    'comment_like' as table_name,
    COUNT(*) as total_likes,
    COUNT(DISTINCT commentId) as commented_articles,
    COUNT(DISTINCT userId) as active_users
FROM comment_like;

-- 查看热门评论（按点赞数排序）
SELECT 
    c.id,
    c.content,
    c.likeCount,
    c.createTime,
    b.bookName,
    u.name as userName
FROM book_comment c
JOIN book b ON c.bookId = b.id
JOIN user u ON c.userId = u.id
WHERE c.parentId IS NULL
ORDER BY c.likeCount DESC
LIMIT 5;

-- 查看评论层级结构
SELECT 
    c1.id as comment_id,
    c1.content as comment_content,
    c1.likeCount,
    c2.id as reply_id,
    c2.content as reply_content,
    c2.likeCount as reply_likes
FROM book_comment c1
LEFT JOIN book_comment c2 ON c1.id = c2.parentId
WHERE c1.parentId IS NULL
ORDER BY c1.createTime DESC, c2.createTime ASC
LIMIT 10;

