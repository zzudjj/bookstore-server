package com.huang.store.enums;

/**
 * 性别枚举
 * 
 * @author 系统管理员
 * @date 2024-01-01
 * @description 用户性别的枚举定义
 */
public enum GenderEnum {
    
    /**
     * 男性
     */
    MALE("男", "男性"),
    
    /**
     * 女性
     */
    FEMALE("女", "女性"),
    
    /**
     * 未知
     */
    UNKNOWN("未知", "未知性别");
    
    /**
     * 性别值
     */
    private final String value;
    
    /**
     * 性别描述
     */
    private final String description;
    
    /**
     * 构造函数
     */
    GenderEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }
    
    /**
     * 获取性别值
     */
    public String getValue() {
        return value;
    }
    
    /**
     * 获取性别描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据性别值获取枚举
     */
    public static GenderEnum fromValue(String value) {
        for (GenderEnum gender : GenderEnum.values()) {
            if (gender.getValue().equals(value)) {
                return gender;
            }
        }
        return UNKNOWN; // 默认返回未知
    }
    
    /**
     * 检查是否为有效的性别值
     */
    public static boolean isValidGender(String value) {
        for (GenderEnum gender : GenderEnum.values()) {
            if (gender.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取所有性别值
     */
    public static String[] getAllValues() {
        GenderEnum[] enums = GenderEnum.values();
        String[] values = new String[enums.length];
        for (int i = 0; i < enums.length; i++) {
            values[i] = enums[i].getValue();
        }
        return values;
    }
    
    @Override
    public String toString() {
        return value;
    }
}
