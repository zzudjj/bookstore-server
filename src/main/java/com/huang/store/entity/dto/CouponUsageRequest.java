package com.huang.store.entity.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 优惠券使用请求DTO
 * 
 * @author: 黄龙
 * @date: 2024/12/01
 * @description: 用户使用优惠券时的请求参数
 */
@Data
public class CouponUsageRequest {
    
    /**
     * 优惠券码
     */
    private String couponCode;
    
    /**
     * 订单金额
     */
    private BigDecimal orderAmount;
    
    /**
     * 用户账号
     */
    private String account;
    
    /**
     * 订单号
     */
    private String orderId;
}
