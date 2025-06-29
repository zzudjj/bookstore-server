package com.huang.store.common;

/**
 * 响应状态码常量类
 * 
 * @author 系统管理员
 * @date 2024-01-01
 * @description 统一管理API响应状态码
 */
public class ResponseCode {
    
    // ========================================
    // 成功状态码 (2xx)
    // ========================================
    
    /**
     * 操作成功
     */
    public static final int SUCCESS = 200;
    
    /**
     * 创建成功
     */
    public static final int CREATED = 201;
    
    /**
     * 接受请求，但处理未完成
     */
    public static final int ACCEPTED = 202;
    
    // ========================================
    // 客户端错误状态码 (4xx)
    // ========================================
    
    /**
     * 请求参数错误
     */
    public static final int BAD_REQUEST = 400;
    
    /**
     * 未授权访问
     */
    public static final int UNAUTHORIZED = 401;
    
    /**
     * 禁止访问
     */
    public static final int FORBIDDEN = 403;
    
    /**
     * 资源不存在
     */
    public static final int NOT_FOUND = 404;
    
    /**
     * 请求方法不允许
     */
    public static final int METHOD_NOT_ALLOWED = 405;
    
    /**
     * 请求超时
     */
    public static final int REQUEST_TIMEOUT = 408;
    
    /**
     * 资源冲突
     */
    public static final int CONFLICT = 409;
    
    /**
     * 请求实体过大
     */
    public static final int PAYLOAD_TOO_LARGE = 413;
    
    /**
     * 请求过于频繁
     */
    public static final int TOO_MANY_REQUESTS = 429;
    
    // ========================================
    // 服务器错误状态码 (5xx)
    // ========================================
    
    /**
     * 服务器内部错误
     */
    public static final int INTERNAL_SERVER_ERROR = 500;
    
    /**
     * 功能未实现
     */
    public static final int NOT_IMPLEMENTED = 501;
    
    /**
     * 网关错误
     */
    public static final int BAD_GATEWAY = 502;
    
    /**
     * 服务不可用
     */
    public static final int SERVICE_UNAVAILABLE = 503;
    
    /**
     * 网关超时
     */
    public static final int GATEWAY_TIMEOUT = 504;
    
    // ========================================
    // 业务状态码 (自定义)
    // ========================================
    
    /**
     * 用户不存在
     */
    public static final int USER_NOT_FOUND = 1001;
    
    /**
     * 用户已存在
     */
    public static final int USER_ALREADY_EXISTS = 1002;
    
    /**
     * 密码错误
     */
    public static final int INVALID_PASSWORD = 1003;
    
    /**
     * 账号被禁用
     */
    public static final int ACCOUNT_DISABLED = 1004;
    
    /**
     * 图书不存在
     */
    public static final int BOOK_NOT_FOUND = 2001;
    
    /**
     * 图书库存不足
     */
    public static final int INSUFFICIENT_STOCK = 2002;
    
    /**
     * 图书已下架
     */
    public static final int BOOK_OFF_SHELF = 2003;
    
    /**
     * 订单不存在
     */
    public static final int ORDER_NOT_FOUND = 3001;
    
    /**
     * 订单状态错误
     */
    public static final int INVALID_ORDER_STATUS = 3002;
    
    /**
     * 购物车为空
     */
    public static final int CART_EMPTY = 4001;
    
    /**
     * 购物车商品不存在
     */
    public static final int CART_ITEM_NOT_FOUND = 4002;
    
    /**
     * 文件上传失败
     */
    public static final int FILE_UPLOAD_FAILED = 5001;
    
    /**
     * 文件类型不支持
     */
    public static final int UNSUPPORTED_FILE_TYPE = 5002;
    
    /**
     * 文件大小超限
     */
    public static final int FILE_SIZE_EXCEEDED = 5003;
    
    // ========================================
    // 响应消息常量
    // ========================================
    
    public static class Message {
        public static final String SUCCESS = "操作成功";
        public static final String CREATED = "创建成功";
        public static final String BAD_REQUEST = "请求参数错误";
        public static final String UNAUTHORIZED = "未授权访问";
        public static final String FORBIDDEN = "禁止访问";
        public static final String NOT_FOUND = "资源不存在";
        public static final String INTERNAL_SERVER_ERROR = "服务器内部错误";
        public static final String USER_NOT_FOUND = "用户不存在";
        public static final String USER_ALREADY_EXISTS = "用户已存在";
        public static final String INVALID_PASSWORD = "密码错误";
        public static final String ACCOUNT_DISABLED = "账号已被禁用";
        public static final String BOOK_NOT_FOUND = "图书不存在";
        public static final String INSUFFICIENT_STOCK = "图书库存不足";
        public static final String BOOK_OFF_SHELF = "图书已下架";
        public static final String ORDER_NOT_FOUND = "订单不存在";
        public static final String INVALID_ORDER_STATUS = "订单状态错误";
        public static final String CART_EMPTY = "购物车为空";
        public static final String CART_ITEM_NOT_FOUND = "购物车商品不存在";
        public static final String FILE_UPLOAD_FAILED = "文件上传失败";
        public static final String UNSUPPORTED_FILE_TYPE = "文件类型不支持";
        public static final String FILE_SIZE_EXCEEDED = "文件大小超限";
    }
    
    /**
     * 私有构造函数，防止实例化
     */
    private ResponseCode() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
