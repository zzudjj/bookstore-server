package com.huang.store.enums;

/**
 * 订单状态枚举 - 统一标准版本
 *
 * @author 系统管理员
 * @date 2024-01-01
 * @description 订单状态的统一枚举定义
 */
public enum OrderStatusEnum {

    /**
     * 待付款 - 订单已创建，等待用户付款
     */
    PENDING_PAYMENT("待付款", "订单已创建，等待付款", "#E6A23C"),

    /**
     * 已付款 - 用户已付款，等待商家发货
     */
    PAID("已付款", "订单已付款，等待发货", "#409EFF"),

    /**
     * 已发货 - 商家已发货，商品在运输途中
     */
    SHIPPED("已发货", "商品已发货，正在配送", "#67C23A"),

    /**
     * 已完成 - 订单已完成，交易结束
     */
    COMPLETED("已完成", "订单已完成", "#909399"),

    /**
     * 已取消 - 订单已取消
     */
    CANCELLED("已取消", "订单已取消", "#F56C6C");

    /**
     * 状态值
     */
    private final String value;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 状态颜色
     */
    private final String color;

    /**
     * 构造函数
     */
    OrderStatusEnum(String value, String description, String color) {
        this.value = value;
        this.description = description;
        this.color = color;
    }
    
    /**
     * 获取状态值
     */
    public String getValue() {
        return value;
    }
    
    /**
     * 获取状态描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 获取状态颜色
     */
    public String getColor() {
        return color;
    }
    
    /**
     * 根据状态值获取枚举
     */
    public static OrderStatusEnum fromValue(String value) {
        for (OrderStatusEnum status : OrderStatusEnum.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的订单状态: " + value);
    }
    
    /**
     * 检查是否为有效的订单状态
     */
    public static boolean isValidStatus(String value) {
        try {
            fromValue(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * 获取所有状态值
     */
    public static String[] getAllValues() {
        OrderStatusEnum[] enums = OrderStatusEnum.values();
        String[] values = new String[enums.length];
        for (int i = 0; i < enums.length; i++) {
            values[i] = enums[i].getValue();
        }
        return values;
    }

    /**
     * 检查状态流转是否合法
     */
    public static boolean canTransitionTo(String currentStatus, String targetStatus) {
        if (currentStatus == null || targetStatus == null) {
            return false;
        }

        // 定义允许的状态流转
        switch (currentStatus) {
            case "待付款":
                return "已付款".equals(targetStatus) || "已取消".equals(targetStatus);
            case "已付款":
                return "已发货".equals(targetStatus) || "已取消".equals(targetStatus);
            case "已发货":
                return "已完成".equals(targetStatus);
            case "已完成":
            case "已取消":
                return false; // 终态，不允许再转换
            default:
                return false;
        }
    }

    /**
     * 获取状态对应的图标
     */
    public static String getStatusIcon(String status) {
        switch (status) {
            case "待付款": return "el-icon-time";
            case "已付款": return "el-icon-box";
            case "已发货": return "el-icon-truck";
            case "已完成": return "el-icon-check";
            case "已取消": return "el-icon-close";
            default: return "el-icon-info";
        }
    }
    
    @Override
    public String toString() {
        return value;
    }
}
