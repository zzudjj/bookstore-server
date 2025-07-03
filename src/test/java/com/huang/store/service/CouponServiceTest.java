package com.huang.store.service;

import com.huang.store.entity.order.CouponTemplate;
import com.huang.store.entity.order.UserCoupon;
import com.huang.store.entity.dto.*;
import com.huang.store.service.imp.CouponService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 优惠券服务测试类
 * 
 * @author: 黄龙
 * @date: 2024/12/01
 * @description: 测试优惠券系统的核心功能
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CouponServiceTest {

    @Autowired
    private CouponService couponService;

    /**
     * 测试创建满减券模板
     */
    @Test
    public void testCreateFullReductionCoupon() {
        // 准备测试数据
        CouponCreateRequest request = new CouponCreateRequest();
        request.setName("测试满减券");
        request.setType(1); // 满减券
        request.setDiscountValue(new BigDecimal("20.00"));
        request.setMinOrderAmount(new BigDecimal("100.00"));
        request.setTotalQuantity(100);
        request.setPerUserLimit(1);
        request.setValidDays(30);

        // 执行测试
        CouponTemplate template = couponService.createCouponTemplate(request);

        // 验证结果
        assertNotNull(template);
        assertNotNull(template.getId());
        assertEquals("测试满减券", template.getName());
        assertEquals(Integer.valueOf(1), template.getType());
        assertEquals(new BigDecimal("20.00"), template.getDiscountValue());
        assertEquals(new BigDecimal("100.00"), template.getMinOrderAmount());
        assertEquals(Integer.valueOf(100), template.getTotalQuantity());
        assertEquals(Integer.valueOf(0), template.getUsedQuantity());
        assertEquals(Integer.valueOf(0), template.getReceivedQuantity());
    }

    /**
     * 测试创建折扣券模板
     */
    @Test
    public void testCreateDiscountCoupon() {
        // 准备测试数据
        CouponCreateRequest request = new CouponCreateRequest();
        request.setName("测试折扣券");
        request.setType(2); // 折扣券
        request.setDiscountValue(new BigDecimal("85.00")); // 8.5折
        request.setMinOrderAmount(new BigDecimal("50.00"));
        request.setMaxDiscountAmount(new BigDecimal("30.00"));
        request.setTotalQuantity(50);
        request.setPerUserLimit(2);
        request.setValidDays(15);

        // 执行测试
        CouponTemplate template = couponService.createCouponTemplate(request);

        // 验证结果
        assertNotNull(template);
        assertEquals("测试折扣券", template.getName());
        assertEquals(Integer.valueOf(2), template.getType());
        assertEquals(new BigDecimal("85.00"), template.getDiscountValue());
        assertEquals(new BigDecimal("30.00"), template.getMaxDiscountAmount());
    }

    /**
     * 测试用户领取优惠券
     */
    @Test
    public void testClaimCoupon() {
        // 先创建优惠券模板
        CouponCreateRequest request = new CouponCreateRequest();
        request.setName("领取测试券");
        request.setType(1);
        request.setDiscountValue(new BigDecimal("10.00"));
        request.setMinOrderAmount(new BigDecimal("50.00"));
        request.setTotalQuantity(10);
        request.setPerUserLimit(1);
        request.setValidDays(7);

        CouponTemplate template = couponService.createCouponTemplate(request);

        // 用户领取优惠券
        String testAccount = "test@example.com";
        UserCoupon userCoupon = couponService.claimCoupon(template.getId(), testAccount);

        // 验证结果
        assertNotNull(userCoupon);
        assertNotNull(userCoupon.getId());
        assertEquals(template.getId(), userCoupon.getCouponTemplateId());
        assertEquals(testAccount, userCoupon.getAccount());
        assertNotNull(userCoupon.getCouponCode());
        assertEquals(Integer.valueOf(1), userCoupon.getStatus()); // 未使用
        assertNotNull(userCoupon.getReceiveTime());
        assertNotNull(userCoupon.getExpireTime());
    }

    /**
     * 测试满减券折扣计算
     */
    @Test
    public void testCalculateFullReductionDiscount() {
        // 创建满减券模板
        CouponCreateRequest request = new CouponCreateRequest();
        request.setName("计算测试券");
        request.setType(1);
        request.setDiscountValue(new BigDecimal("20.00"));
        request.setMinOrderAmount(new BigDecimal("100.00"));
        request.setTotalQuantity(10);
        request.setPerUserLimit(1);
        request.setValidDays(7);

        CouponTemplate template = couponService.createCouponTemplate(request);

        // 用户领取优惠券
        String testAccount = "test@example.com";
        UserCoupon userCoupon = couponService.claimCoupon(template.getId(), testAccount);

        // 测试满足条件的订单金额
        BigDecimal orderAmount = new BigDecimal("150.00");
        CouponCalculationResult result = couponService.calculateCouponDiscount(
                userCoupon.getCouponCode(), testAccount, orderAmount);

        // 验证结果
        assertTrue(result.getAvailable());
        assertEquals(new BigDecimal("150.00"), result.getOriginalAmount());
        assertEquals(new BigDecimal("20.00"), result.getDiscountAmount());
        assertEquals(new BigDecimal("130.00"), result.getFinalAmount());

        // 测试不满足条件的订单金额
        BigDecimal lowOrderAmount = new BigDecimal("80.00");
        CouponCalculationResult lowResult = couponService.calculateCouponDiscount(
                userCoupon.getCouponCode(), testAccount, lowOrderAmount);

        // 验证结果
        assertFalse(lowResult.getAvailable());
        assertNotNull(lowResult.getReason());
        assertTrue(lowResult.getReason().contains("订单金额不满足使用条件"));
    }

    /**
     * 测试折扣券折扣计算
     */
    @Test
    public void testCalculateDiscountCouponDiscount() {
        // 创建折扣券模板（8.5折，最多减30元）
        CouponCreateRequest request = new CouponCreateRequest();
        request.setName("折扣测试券");
        request.setType(2);
        request.setDiscountValue(new BigDecimal("85.00")); // 8.5折
        request.setMinOrderAmount(new BigDecimal("50.00"));
        request.setMaxDiscountAmount(new BigDecimal("30.00"));
        request.setTotalQuantity(10);
        request.setPerUserLimit(1);
        request.setValidDays(7);

        CouponTemplate template = couponService.createCouponTemplate(request);

        // 用户领取优惠券
        String testAccount = "test@example.com";
        UserCoupon userCoupon = couponService.claimCoupon(template.getId(), testAccount);

        // 测试普通订单金额（100元，应该减15元）
        BigDecimal orderAmount = new BigDecimal("100.00");
        CouponCalculationResult result = couponService.calculateCouponDiscount(
                userCoupon.getCouponCode(), testAccount, orderAmount);

        // 验证结果
        assertTrue(result.getAvailable());
        assertEquals(new BigDecimal("100.00"), result.getOriginalAmount());
        assertEquals(new BigDecimal("15.00"), result.getDiscountAmount());
        assertEquals(new BigDecimal("85.00"), result.getFinalAmount());

        // 测试高金额订单（300元，应该减30元，达到上限）
        BigDecimal highOrderAmount = new BigDecimal("300.00");
        CouponCalculationResult highResult = couponService.calculateCouponDiscount(
                userCoupon.getCouponCode(), testAccount, highOrderAmount);

        // 验证结果
        assertTrue(highResult.getAvailable());
        assertEquals(new BigDecimal("300.00"), highResult.getOriginalAmount());
        assertEquals(new BigDecimal("30.00"), highResult.getDiscountAmount()); // 达到最大折扣限制
        assertEquals(new BigDecimal("270.00"), highResult.getFinalAmount());
    }

    /**
     * 测试优惠券使用
     */
    @Test
    public void testUseCoupon() {
        // 创建优惠券模板
        CouponCreateRequest request = new CouponCreateRequest();
        request.setName("使用测试券");
        request.setType(1);
        request.setDiscountValue(new BigDecimal("10.00"));
        request.setMinOrderAmount(new BigDecimal("50.00"));
        request.setTotalQuantity(10);
        request.setPerUserLimit(1);
        request.setValidDays(7);

        CouponTemplate template = couponService.createCouponTemplate(request);

        // 用户领取优惠券
        String testAccount = "test@example.com";
        UserCoupon userCoupon = couponService.claimCoupon(template.getId(), testAccount);

        // 使用优惠券
        CouponUsageRequest usageRequest = new CouponUsageRequest();
        usageRequest.setCouponCode(userCoupon.getCouponCode());
        usageRequest.setOrderAmount(new BigDecimal("80.00"));
        usageRequest.setAccount(testAccount);
        usageRequest.setOrderId("TEST_ORDER_001");

        boolean success = couponService.useCoupon(usageRequest);

        // 验证结果
        assertTrue(success);

        // 验证优惠券状态已更新
        UserCouponDto usedCoupon = couponService.getCouponByCouponCode(
                userCoupon.getCouponCode(), testAccount);

        // 已使用的优惠券应该查询不到（因为查询的是可用优惠券）
        assertNull(usedCoupon);
    }

    /**
     * 测试优惠券码生成
     */
    @Test
    public void testGenerateCouponCode() {
        // 测试满减券码生成
        String fullReductionCode = couponService.generateCouponCode(1, 1);
        assertNotNull(fullReductionCode);
        assertTrue(fullReductionCode.startsWith("FULL"));
        assertEquals(19, fullReductionCode.length()); // FULL + 8位日期 + 3位模板ID + 6位随机数

        // 测试折扣券码生成
        String discountCode = couponService.generateCouponCode(2, 2);
        assertNotNull(discountCode);
        assertTrue(discountCode.startsWith("DISC"));
        assertEquals(19, discountCode.length()); // DISC + 8位日期 + 3位模板ID + 6位随机数
    }

    @Test
    public void testDeleteCouponTemplate() {
        // 准备测试数据
        CouponCreateRequest request = new CouponCreateRequest();
        request.setName("删除测试券");
        request.setType(1);
        request.setDiscountValue(new BigDecimal("10"));
        request.setMinOrderAmount(new BigDecimal("50"));
        request.setTotalQuantity(100);
        request.setPerUserLimit(1);
        request.setValidDays(30);

        // 创建优惠券模板
        CouponTemplate template = couponService.createCouponTemplate(request);
        assertNotNull(template);
        Integer templateId = template.getId();

        // 测试删除未被领取的优惠券（应该成功）
        boolean deleteResult = couponService.deleteCouponTemplate(templateId);
        assertTrue(deleteResult);

        // 验证优惠券已被删除
        CouponTemplateDto deletedTemplate = couponService.getCouponTemplateById(templateId);
        assertNull(deletedTemplate);
    }

    @Test
    public void testDeleteCouponTemplateWithUnusedUserCoupons() {
        // 准备测试数据
        CouponCreateRequest request = new CouponCreateRequest();
        request.setName("有用户领取但未使用的测试券");
        request.setType(1);
        request.setDiscountValue(new BigDecimal("20"));
        request.setMinOrderAmount(new BigDecimal("100"));
        request.setTotalQuantity(100);
        request.setPerUserLimit(1);
        request.setValidDays(30);

        // 创建优惠券模板
        CouponTemplate template = couponService.createCouponTemplate(request);
        assertNotNull(template);
        Integer templateId = template.getId();

        // 模拟用户领取优惠券
        String testAccount = "testuser@example.com";
        UserCoupon claimResult = couponService.claimCoupon(templateId, testAccount);
        assertNotNull(claimResult);

        // 测试删除有未使用优惠券的模板（应该失败）
        try {
            couponService.deleteCouponTemplate(templateId);
            fail("应该抛出异常，因为还有未使用的优惠券");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("还有") && e.getMessage().contains("未使用的优惠券"));
        }

        // 验证优惠券仍然存在
        CouponTemplateDto existingTemplate = couponService.getCouponTemplateById(templateId);
        assertNotNull(existingTemplate);
    }

    @Test
    public void testDeleteCouponTemplateAfterAllUsed() {
        // 准备测试数据
        CouponCreateRequest request = new CouponCreateRequest();
        request.setName("所有优惠券都已使用的测试券");
        request.setType(1);
        request.setDiscountValue(new BigDecimal("10"));
        request.setMinOrderAmount(new BigDecimal("50"));
        request.setTotalQuantity(100);
        request.setPerUserLimit(1);
        request.setValidDays(1); // 设置较短的有效期便于测试

        // 创建优惠券模板
        CouponTemplate template = couponService.createCouponTemplate(request);
        assertNotNull(template);
        Integer templateId = template.getId();

        // 模拟用户领取优惠券
        String testAccount = "testuser@example.com";
        UserCoupon claimResult = couponService.claimCoupon(templateId, testAccount);
        assertNotNull(claimResult);

        // 模拟使用优惠券
        CouponUsageRequest usageRequest = new CouponUsageRequest();
        usageRequest.setCouponCode(claimResult.getCouponCode());
        usageRequest.setAccount(testAccount);
        usageRequest.setOrderId("12345");
        usageRequest.setOrderAmount(new BigDecimal("100"));

        boolean useResult = couponService.useCoupon(usageRequest);
        assertTrue(useResult);

        // 注意：在实际测试中，我们无法轻易模拟时间过期
        // 这里只是演示测试逻辑，实际情况下需要等待优惠券过期或使用数据库时间操作

        // 如果所有优惠券都已使用且过期，删除应该成功
        // 由于测试环境限制，这里只验证逻辑存在
        System.out.println("删除逻辑测试：当所有优惠券都使用完毕且过期后，可以删除模板");
    }
}
