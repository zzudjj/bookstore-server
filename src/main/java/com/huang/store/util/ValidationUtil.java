package com.huang.store.util;

import com.huang.store.common.Constants;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * 参数验证工具类
 * 
 * @author 系统管理员
 * @date 2024-01-01
 * @description 提供常用的参数验证方法
 */
public class ValidationUtil {
    
    /**
     * 邮箱正则表达式
     */
    private static final String EMAIL_PATTERN = 
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    
    /**
     * 编译后的邮箱正则
     */
    private static final Pattern EMAIL_REGEX = Pattern.compile(EMAIL_PATTERN);
    
    /**
     * 验证字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 验证字符串是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /**
     * 验证邮箱格式
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        return EMAIL_REGEX.matcher(email).matches();
    }
    
    /**
     * 验证密码强度
     */
    public static boolean isValidPassword(String password) {
        if (isEmpty(password)) {
            return false;
        }
        return password.length() >= Constants.PasswordRule.MIN_LENGTH 
            && password.length() <= Constants.PasswordRule.MAX_LENGTH;
    }
    
    /**
     * 验证用户名长度
     */
    public static boolean isValidUsername(String username) {
        if (isEmpty(username)) {
            return false;
        }
        return username.length() <= Constants.UsernameRule.MAX_LENGTH;
    }
    
    /**
     * 验证正整数
     */
    public static boolean isPositiveInteger(Integer value) {
        return value != null && value > 0;
    }
    
    /**
     * 验证非负整数
     */
    public static boolean isNonNegativeInteger(Integer value) {
        return value != null && value >= 0;
    }
    
    /**
     * 验证分页参数
     */
    public static boolean isValidPageParam(Integer page, Integer pageSize) {
        return isPositiveInteger(page) 
            && isPositiveInteger(pageSize)
            && pageSize <= Constants.Pagination.MAX_PAGE_SIZE;
    }
    
    /**
     * 验证图书ID
     */
    public static boolean isValidBookId(Integer bookId) {
        return isPositiveInteger(bookId);
    }
    
    /**
     * 验证数量
     */
    public static boolean isValidQuantity(Integer quantity) {
        return isPositiveInteger(quantity);
    }
    
    /**
     * 验证订单状态
     */
    public static boolean isValidOrderStatus(String status) {
        if (isEmpty(status)) {
            return false;
        }
        // 这里可以根据实际的订单状态枚举来验证
        String[] validStatuses = {
            Constants.OrderStatus.PENDING_PAYMENT,
            Constants.OrderStatus.PENDING_SHIPMENT,
            Constants.OrderStatus.SHIPPED,
            Constants.OrderStatus.DELIVERED,
            Constants.OrderStatus.COMPLETED,
            Constants.OrderStatus.CANCELLED,
            Constants.OrderStatus.REFUNDED
        };
        
        for (String validStatus : validStatuses) {
            if (validStatus.equals(status)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 验证性别
     */
    public static boolean isValidGender(String gender) {
        if (isEmpty(gender)) {
            return true; // 性别可以为空
        }
        return Constants.Gender.MALE.equals(gender) 
            || Constants.Gender.FEMALE.equals(gender)
            || Constants.Gender.UNKNOWN.equals(gender);
    }
    
    /**
     * 验证文件扩展名
     */
    public static boolean isValidImageExtension(String filename) {
        if (isEmpty(filename)) {
            return false;
        }
        
        String extension = getFileExtension(filename).toLowerCase();
        return Constants.AllowedImageTypes.JPG.equals(extension)
            || Constants.AllowedImageTypes.JPEG.equals(extension)
            || Constants.AllowedImageTypes.PNG.equals(extension)
            || Constants.AllowedImageTypes.GIF.equals(extension)
            || Constants.AllowedImageTypes.WEBP.equals(extension);
    }
    
    /**
     * 获取文件扩展名
     */
    public static String getFileExtension(String filename) {
        if (isEmpty(filename)) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        
        return filename.substring(lastDotIndex + 1);
    }
    
    /**
     * 验证字符串长度
     */
    public static boolean isValidLength(String str, int maxLength) {
        if (str == null) {
            return true;
        }
        return str.length() <= maxLength;
    }
    
    /**
     * 验证字符串长度范围
     */
    public static boolean isValidLength(String str, int minLength, int maxLength) {
        if (str == null) {
            return minLength == 0;
        }
        return str.length() >= minLength && str.length() <= maxLength;
    }
    
    /**
     * 验证ISBN格式（简单验证）
     */
    public static boolean isValidISBN(String isbn) {
        if (isEmpty(isbn)) {
            return false;
        }
        // 简单验证：10位或13位数字
        return isbn.matches("^\\d{10}$") || isbn.matches("^\\d{13}$");
    }
    
    /**
     * 私有构造函数，防止实例化
     */
    private ValidationUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
