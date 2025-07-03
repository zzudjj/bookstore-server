package com.huang.store.entity.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 优惠券计算结果DTO
 * 
 * @author: 黄龙
 * @date: 2024/12/01
 * @description: 优惠券折扣计算结果
 */
@Data
public class CouponCalculationResult {
    
    /**
     * 是否可用
     */
    private Boolean available;
    
    /**
     * 不可用原因
     */
    private String reason;
    
    /**
     * 原始金额
     */
    private BigDecimal originalAmount;
    
    /**
     * 折扣金额
     */
    private BigDecimal discountAmount;
    
    /**
     * 最终金额
     */
    private BigDecimal finalAmount;
    
    /**
     * 优惠券信息
     */
    private UserCouponDto couponInfo;
    
    /**
     * 创建可用的计算结果
     */
    public static CouponCalculationResult available(BigDecimal originalAmount, BigDecimal discountAmount, UserCouponDto couponInfo) {
        CouponCalculationResult result = new CouponCalculationResult();
        result.setAvailable(true);
        result.setOriginalAmount(originalAmount);
        result.setDiscountAmount(discountAmount);
        result.setFinalAmount(originalAmount.subtract(discountAmount));
        result.setCouponInfo(couponInfo);
        return result;
    }
    
    /**
     * 创建不可用的计算结果
     */
    public static CouponCalculationResult unavailable(String reason, BigDecimal originalAmount) {
        CouponCalculationResult result = new CouponCalculationResult();
        result.setAvailable(false);
        result.setReason(reason);
        result.setOriginalAmount(originalAmount);
        result.setDiscountAmount(BigDecimal.ZERO);
        result.setFinalAmount(originalAmount);
        return result;
    }
}
