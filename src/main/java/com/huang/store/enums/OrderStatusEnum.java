package com.huang.store.enums;

/**
 * 订单状态枚举
 * 
 * @author 系统管理员
 * @date 2024-01-01
 * @description 订单状态的枚举定义
 */
public enum OrderStatusEnum {
    
    /**
     * 待付款
     */
    PENDING_PAYMENT("待付款", "用户已下单，等待付款"),
    
    /**
     * 待发货
     */
    PENDING_SHIPMENT("待发货", "用户已付款，等待商家发货"),
    
    /**
     * 已发货
     */
    SHIPPED("已发货", "商家已发货，商品在运输途中"),
    
    /**
     * 已送达
     */
    DELIVERED("已送达", "商品已送达，等待用户确认收货"),
    
    /**
     * 已完成
     */
    COMPLETED("已完成", "订单已完成，交易结束"),
    
    /**
     * 已取消
     */
    CANCELLED("已取消", "订单已取消"),
    
    /**
     * 已退款
     */
    REFUNDED("已退款", "订单已退款");
    
    /**
     * 状态值
     */
    private final String value;
    
    /**
     * 状态描述
     */
    private final String description;
    
    /**
     * 构造函数
     */
    OrderStatusEnum(String value, String description) {
        this.value = value;
        this.description = description;
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
    
    @Override
    public String toString() {
        return value;
    }
}
