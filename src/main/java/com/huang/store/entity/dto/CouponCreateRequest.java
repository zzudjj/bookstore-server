package com.huang.store.entity.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建优惠券请求DTO
 * 
 * @author: 黄龙
 * @date: 2024/12/01
 * @description: 管理员创建优惠券时的请求参数
 */
@Data
public class CouponCreateRequest {

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 优惠券类型：1-满减券，2-折扣券
     */
    private Integer type;

    /**
     * 折扣值（满减券为减免金额，折扣券为折扣百分比）
     */
    private BigDecimal discountValue;

    /**
     * 最低消费金额
     */
    private BigDecimal minOrderAmount;

    /**
     * 最大折扣金额（仅折扣券使用）
     */
    private BigDecimal maxDiscountAmount;

    /**
     * 发放总数量
     */
    private Integer totalQuantity;

    /**
     * 每用户限领数量
     */
    private Integer perUserLimit;

    /**
     * 有效天数
     */
    private Integer validDays;
}
