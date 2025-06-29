package com.huang.store.exception;

import com.huang.store.common.ResponseCode;

/**
 * 业务异常类
 * 
 * @author 系统管理员
 * @date 2024-01-01
 * @description 用于处理业务逻辑中的异常情况
 */
public class BusinessException extends RuntimeException {
    
    /**
     * 错误码
     */
    private Integer code;
    
    /**
     * 错误消息
     */
    private String message;
    
    /**
     * 构造函数 - 使用默认错误码
     */
    public BusinessException(String message) {
        super(message);
        this.code = ResponseCode.INTERNAL_SERVER_ERROR;
        this.message = message;
    }
    
    /**
     * 构造函数 - 指定错误码和消息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    /**
     * 构造函数 - 包含原因异常
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResponseCode.INTERNAL_SERVER_ERROR;
        this.message = message;
    }
    
    /**
     * 构造函数 - 完整参数
     */
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
    
    // ========================================
    // 静态工厂方法 - 常用业务异常
    // ========================================
    
    /**
     * 用户不存在异常
     */
    public static BusinessException userNotFound() {
        return new BusinessException(ResponseCode.USER_NOT_FOUND, ResponseCode.Message.USER_NOT_FOUND);
    }
    
    /**
     * 用户已存在异常
     */
    public static BusinessException userAlreadyExists() {
        return new BusinessException(ResponseCode.USER_ALREADY_EXISTS, ResponseCode.Message.USER_ALREADY_EXISTS);
    }
    
    /**
     * 密码错误异常
     */
    public static BusinessException invalidPassword() {
        return new BusinessException(ResponseCode.INVALID_PASSWORD, ResponseCode.Message.INVALID_PASSWORD);
    }
    
    /**
     * 账号被禁用异常
     */
    public static BusinessException accountDisabled() {
        return new BusinessException(ResponseCode.ACCOUNT_DISABLED, ResponseCode.Message.ACCOUNT_DISABLED);
    }
    
    /**
     * 图书不存在异常
     */
    public static BusinessException bookNotFound() {
        return new BusinessException(ResponseCode.BOOK_NOT_FOUND, ResponseCode.Message.BOOK_NOT_FOUND);
    }
    
    /**
     * 图书库存不足异常
     */
    public static BusinessException insufficientStock() {
        return new BusinessException(ResponseCode.INSUFFICIENT_STOCK, ResponseCode.Message.INSUFFICIENT_STOCK);
    }
    
    /**
     * 图书已下架异常
     */
    public static BusinessException bookOffShelf() {
        return new BusinessException(ResponseCode.BOOK_OFF_SHELF, ResponseCode.Message.BOOK_OFF_SHELF);
    }
    
    /**
     * 订单不存在异常
     */
    public static BusinessException orderNotFound() {
        return new BusinessException(ResponseCode.ORDER_NOT_FOUND, ResponseCode.Message.ORDER_NOT_FOUND);
    }
    
    /**
     * 订单状态错误异常
     */
    public static BusinessException invalidOrderStatus() {
        return new BusinessException(ResponseCode.INVALID_ORDER_STATUS, ResponseCode.Message.INVALID_ORDER_STATUS);
    }
    
    /**
     * 购物车为空异常
     */
    public static BusinessException cartEmpty() {
        return new BusinessException(ResponseCode.CART_EMPTY, ResponseCode.Message.CART_EMPTY);
    }
    
    /**
     * 购物车商品不存在异常
     */
    public static BusinessException cartItemNotFound() {
        return new BusinessException(ResponseCode.CART_ITEM_NOT_FOUND, ResponseCode.Message.CART_ITEM_NOT_FOUND);
    }
    
    /**
     * 文件上传失败异常
     */
    public static BusinessException fileUploadFailed() {
        return new BusinessException(ResponseCode.FILE_UPLOAD_FAILED, ResponseCode.Message.FILE_UPLOAD_FAILED);
    }
    
    /**
     * 文件类型不支持异常
     */
    public static BusinessException unsupportedFileType() {
        return new BusinessException(ResponseCode.UNSUPPORTED_FILE_TYPE, ResponseCode.Message.UNSUPPORTED_FILE_TYPE);
    }
    
    // ========================================
    // Getter and Setter
    // ========================================
    
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return "BusinessException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
