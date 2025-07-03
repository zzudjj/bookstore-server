package com.huang.store.service.imp;

import com.huang.store.entity.order.CouponTemplate;
import com.huang.store.entity.order.UserCoupon;
import com.huang.store.entity.dto.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券服务接口
 * 
 * @author: 黄龙
 * @date: 2024/12/01
 * @description: 优惠券业务逻辑接口
 */
public interface CouponService {
    
    // ==================== 优惠券模板管理 ====================
    
    /**
     * 创建优惠券模板（管理员）
     */
    CouponTemplate createCouponTemplate(CouponCreateRequest request);
    
    /**
     * 获取所有优惠券模板（管理员）
     */
    List<CouponTemplateDto> getAllCouponTemplates();
    
    /**
     * 获取启用的优惠券模板
     */
    List<CouponTemplateDto> getEnabledCouponTemplates();
    
    /**
     * 根据ID获取优惠券模板
     */
    CouponTemplateDto getCouponTemplateById(Integer id);
    
    /**
     * 更新优惠券模板（管理员）
     */
    boolean updateCouponTemplate(Integer id, CouponCreateRequest request);
    
    /**
     * 启用/停用优惠券模板（管理员）
     */
    boolean updateCouponTemplateStatus(Integer id, Integer status);
    
    /**
     * 删除优惠券模板（管理员）
     */
    boolean deleteCouponTemplate(Integer id);
    
    // ==================== 用户优惠券管理 ====================
    
    /**
     * 用户领取优惠券
     */
    UserCoupon claimCoupon(Integer templateId, String account);
    
    /**
     * 获取用户的所有优惠券
     */
    List<UserCouponDto> getUserCoupons(String account);
    
    /**
     * 获取用户可用的优惠券
     */
    List<UserCouponDto> getAvailableUserCoupons(String account);
    
    /**
     * 根据优惠券码获取优惠券信息
     */
    UserCouponDto getCouponByCouponCode(String couponCode, String account);
    
    // ==================== 优惠券使用 ====================
    
    /**
     * 计算优惠券折扣
     */
    CouponCalculationResult calculateCouponDiscount(String couponCode, String account, BigDecimal orderAmount);
    
    /**
     * 使用优惠券
     */
    boolean useCoupon(CouponUsageRequest request);
    
    /**
     * 验证优惠券是否可用
     */
    boolean validateCoupon(String couponCode, String account, BigDecimal orderAmount);
    
    // ==================== 工具方法 ====================
    
    /**
     * 生成优惠券码
     */
    String generateCouponCode(Integer templateId, Integer type);
    
    /**
     * 标记过期优惠券
     */
    void markExpiredCoupons();
    
    /**
     * 转换优惠券模板为DTO
     */
    CouponTemplateDto convertToTemplateDto(CouponTemplate template);
    
    /**
     * 转换用户优惠券为DTO
     */
    UserCouponDto convertToUserCouponDto(UserCoupon userCoupon, CouponTemplate template);
}
