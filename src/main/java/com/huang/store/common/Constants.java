package com.huang.store.common;

/**
 * 系统常量类
 * 
 * @author 系统管理员
 * @date 2024-01-01
 * @description 统一管理系统中使用的常量
 */
public class Constants {
    
    // ========================================
    // 用户相关常量
    // ========================================
    
    /**
     * 默认用户头像
     */
    public static final String DEFAULT_USER_AVATAR = "/images/default-avatar.png";
    
    /**
     * 用户性别
     */
    public static class Gender {
        public static final String MALE = "男";
        public static final String FEMALE = "女";
        public static final String UNKNOWN = "未知";
    }
    
    /**
     * 用户状态
     */
    public static class UserStatus {
        public static final boolean ENABLED = true;
        public static final boolean DISABLED = false;
    }
    
    /**
     * 用户角色
     */
    public static class UserRole {
        public static final boolean ADMIN = true;
        public static final boolean NORMAL = false;
    }
    
    // ========================================
    // 图书相关常量
    // ========================================
    
    /**
     * 图书状态
     */
    public static class BookStatus {
        public static final boolean ON_SHELF = true;
        public static final boolean OFF_SHELF = false;
    }
    
    /**
     * 图书标签
     */
    public static class BookTag {
        public static final boolean NEW_PRODUCT = true;
        public static final boolean RECOMMEND = true;
        public static final boolean NORMAL = false;
    }
    
    /**
     * 默认图书封面
     */
    public static final String DEFAULT_BOOK_COVER = "/images/default-book-cover.png";
    
    // ========================================
    // 订单相关常量
    // ========================================
    
    /**
     * 订单状态
     */
    public static class OrderStatus {
        public static final String PENDING_PAYMENT = "待付款";
        public static final String PENDING_SHIPMENT = "待发货";
        public static final String SHIPPED = "已发货";
        public static final String DELIVERED = "已送达";
        public static final String COMPLETED = "已完成";
        public static final String CANCELLED = "已取消";
        public static final String REFUNDED = "已退款";
    }
    
    /**
     * 订单删除状态
     */
    public static class OrderDeleteStatus {
        public static final boolean DELETED = true;
        public static final boolean NOT_DELETED = false;
    }
    
    // ========================================
    // 文件上传相关常量
    // ========================================
    
    /**
     * 允许的图片文件类型
     */
    public static class AllowedImageTypes {
        public static final String JPG = "jpg";
        public static final String JPEG = "jpeg";
        public static final String PNG = "png";
        public static final String GIF = "gif";
        public static final String WEBP = "webp";
    }
    
    /**
     * 文件大小限制（字节）
     */
    public static class FileSizeLimit {
        public static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
        public static final long MAX_DOCUMENT_SIZE = 10 * 1024 * 1024; // 10MB
    }
    
    /**
     * 上传路径
     */
    public static class UploadPath {
        public static final String BOOK_IMAGES = "book";
        public static final String USER_AVATARS = "avatar";
        public static final String TEMP = "temp";
    }
    
    // ========================================
    // 分页相关常量
    // ========================================
    
    /**
     * 分页参数
     */
    public static class Pagination {
        public static final int DEFAULT_PAGE = 1;
        public static final int DEFAULT_PAGE_SIZE = 10;
        public static final int MAX_PAGE_SIZE = 100;
        public static final int MIN_PAGE_SIZE = 1;
    }
    
    // ========================================
    // 缓存相关常量
    // ========================================
    
    /**
     * 缓存键前缀
     */
    public static class CacheKey {
        public static final String USER_PREFIX = "user:";
        public static final String BOOK_PREFIX = "book:";
        public static final String ORDER_PREFIX = "order:";
        public static final String CART_PREFIX = "cart:";
    }
    
    /**
     * 缓存过期时间（秒）
     */
    public static class CacheExpire {
        public static final long USER_INFO = 30 * 60; // 30分钟
        public static final long BOOK_INFO = 60 * 60; // 1小时
        public static final long BOOK_LIST = 10 * 60; // 10分钟
        public static final long ORDER_INFO = 15 * 60; // 15分钟
    }
    
    // ========================================
    // 业务规则常量
    // ========================================
    
    /**
     * 密码规则
     */
    public static class PasswordRule {
        public static final int MIN_LENGTH = 6;
        public static final int MAX_LENGTH = 20;
    }
    
    /**
     * 用户名规则
     */
    public static class UsernameRule {
        public static final int MAX_LENGTH = 50;
    }
    
    /**
     * 图书信息规则
     */
    public static class BookRule {
        public static final int MAX_NAME_LENGTH = 100;
        public static final int MAX_AUTHOR_LENGTH = 50;
        public static final int MAX_PUBLISHER_LENGTH = 50;
        public static final int MAX_DESCRIPTION_LENGTH = 2000;
    }
    
    // ========================================
    // 系统配置常量
    // ========================================
    
    /**
     * 系统名称
     */
    public static final String SYSTEM_NAME = "书店管理系统";
    
    /**
     * 系统版本
     */
    public static final String SYSTEM_VERSION = "1.0.0";
    
    /**
     * 默认时区
     */
    public static final String DEFAULT_TIMEZONE = "GMT+8";
    
    /**
     * 日期时间格式
     */
    public static class DateTimeFormat {
        public static final String DATE = "yyyy-MM-dd";
        public static final String DATETIME = "yyyy-MM-dd HH:mm:ss";
        public static final String TIME = "HH:mm:ss";
    }
    
    /**
     * 私有构造函数，防止实例化
     */
    private Constants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
