-- ========================================
-- 图书路径统一迁移脚本
-- 将 book01 文件夹路径统一为 book 文件夹路径
-- 执行前请确保已备份数据库和文件系统
-- ========================================

USE `bookstore`;

-- ========================================
-- 1. 数据库路径更新
-- ========================================

-- 更新 bookimg 表中的图片路径
-- 将 static//image//book01// 替换为 static//image//book//
UPDATE `bookimg` 
SET `imgSrc` = REPLACE(`imgSrc`, 'static//image//book01//', 'static//image//book//') 
WHERE `imgSrc` LIKE '%static//image//book01//%';

-- 更新 bookimg 表中的图片路径（处理正斜杠的情况）
-- 将 static/image/book01/ 替换为 static/image/book/
UPDATE `bookimg` 
SET `imgSrc` = REPLACE(`imgSrc`, 'static/image/book01/', 'static/image/book/') 
WHERE `imgSrc` LIKE '%static/image/book01/%';

-- ========================================
-- 2. 验证更新结果
-- ========================================

-- 查看更新后的图片路径
SELECT 
    isbn,
    imgSrc,
    cover,
    CASE 
        WHEN imgSrc LIKE '%book01%' THEN '需要手动处理'
        WHEN imgSrc LIKE '%static/image/book/%' THEN '已更新'
        ELSE '其他路径'
    END AS status
FROM `bookimg`
ORDER BY isbn, cover DESC;

-- 统计路径分布
SELECT 
    CASE 
        WHEN imgSrc LIKE '%static/image/book/%' AND imgSrc NOT LIKE '%book01%' THEN 'book路径'
        WHEN imgSrc LIKE '%book01%' THEN 'book01路径(需处理)'
        ELSE '其他路径'
    END AS path_type,
    COUNT(*) as count
FROM `bookimg`
GROUP BY path_type;

-- ========================================
-- 3. 文件系统迁移说明
-- ========================================

/*
数据库路径更新完成后，还需要进行文件系统迁移：

1. 手动文件迁移步骤：
   源目录: D://ITsoftware//IDEA//data//Vue//book_01//static//image//book01//
   目标目录: D://ITsoftware//IDEA//data//Vue//book_01//static//image//book//

2. Windows 命令行操作：
   # 创建目标目录（如果不存在）
   mkdir "D:\ITsoftware\IDEA\data\Vue\book_01\static\image\book"
   
   # 移动所有文件
   move "D:\ITsoftware\IDEA\data\Vue\book_01\static\image\book01\*" "D:\ITsoftware\IDEA\data\Vue\book_01\static\image\book\"
   
   # 删除空的 book01 目录
   rmdir "D:\ITsoftware\IDEA\data\Vue\book_01\static\image\book01"

3. Linux/Mac 命令行操作：
   # 创建目标目录（如果不存在）
   mkdir -p "/path/to/static/image/book"
   
   # 移动所有文件
   mv /path/to/static/image/book01/* /path/to/static/image/book/
   
   # 删除空的 book01 目录
   rmdir /path/to/static/image/book01

4. 验证文件迁移：
   - 检查 book 目录下是否有所有图片文件
   - 检查 book01 目录是否已清空
   - 测试图片访问是否正常

5. 注意事项：
   - 执行前请备份整个 static/image 目录
   - 确保应用程序已停止运行
   - 迁移完成后重启应用程序
   - 测试图片上传和显示功能
*/

-- ========================================
-- 4. 回滚脚本（如果需要）
-- ========================================

/*
如果需要回滚到 book01 路径，执行以下 SQL：

UPDATE `bookimg` 
SET `imgSrc` = REPLACE(`imgSrc`, 'static//image//book//', 'static//image//book01//') 
WHERE `imgSrc` LIKE '%static//image//book//%';

UPDATE `bookimg` 
SET `imgSrc` = REPLACE(`imgSrc`, 'static/image/book/', 'static/image/book01/') 
WHERE `imgSrc` LIKE '%static/image/book/%';

同时需要将文件系统中的文件从 book 目录移回 book01 目录。
*/
