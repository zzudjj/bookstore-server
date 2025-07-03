package com.huang.store.controller;

import com.huang.store.entity.order.CouponTemplate;
import com.huang.store.entity.order.UserCoupon;
import com.huang.store.entity.dto.*;
import com.huang.store.service.imp.CouponService;
import com.huang.store.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 优惠券控制器
 * 
 * @author: 黄龙
 * @date: 2024/12/01
 * @description: 优惠券相关API接口
 */
@Controller
@ResponseBody
@RequestMapping("/coupon")
public class CouponController {

    private static final Logger logger = LoggerFactory.getLogger(CouponController.class);

    @Autowired
    private CouponService couponService;

    // ==================== 管理员接口 ====================

    /**
     * 创建优惠券模板（管理员）
     */
    @PostMapping("/admin/template")
    public Map<String, Object> createCouponTemplate(@RequestBody CouponCreateRequest request) {
        try {
            logger.info("管理员创建优惠券模板: {}", request.getName());

            // 参数验证
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                return ResultUtil.resultCode(400, "优惠券名称不能为空");
            }
            if (request.getType() == null || (request.getType() != 1 && request.getType() != 2)) {
                return ResultUtil.resultCode(400, "优惠券类型错误");
            }
            if (request.getDiscountValue() == null || request.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
                return ResultUtil.resultCode(400, "折扣值必须大于0");
            }
            if (request.getTotalQuantity() == null || request.getTotalQuantity() <= 0) {
                return ResultUtil.resultCode(400, "发放数量必须大于0");
            }
            if (request.getValidDays() == null || request.getValidDays() <= 0) {
                return ResultUtil.resultCode(400, "有效天数必须大于0");
            }

            CouponTemplate template = couponService.createCouponTemplate(request);
            return ResultUtil.resultCode(200, "创建成功", template);

        } catch (Exception e) {
            logger.error("创建优惠券模板失败", e);
            return ResultUtil.resultCode(500, "创建失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有优惠券模板（管理员）
     */
    @GetMapping("/admin/templates")
    public Map<String, Object> getAllCouponTemplates() {
        try {
            logger.info("管理员查询所有优惠券模板");
            List<CouponTemplateDto> templates = couponService.getAllCouponTemplates();
            return ResultUtil.resultCode(200, "查询成功", templates);
        } catch (Exception e) {
            logger.error("查询优惠券模板失败", e);
            return ResultUtil.resultCode(500, "查询失败: " + e.getMessage());
        }
    }

    /**
     * 更新优惠券模板（管理员）
     */
    @PutMapping("/admin/template/{id}")
    public Map<String, Object> updateCouponTemplate(@PathVariable Integer id,
            @RequestBody CouponCreateRequest request) {
        try {
            logger.info("管理员更新优惠券模板: {}", id);
            boolean success = couponService.updateCouponTemplate(id, request);
            if (success) {
                return ResultUtil.resultCode(200, "更新成功");
            } else {
                return ResultUtil.resultCode(404, "优惠券模板不存在");
            }
        } catch (Exception e) {
            logger.error("更新优惠券模板失败", e);
            return ResultUtil.resultCode(500, "更新失败: " + e.getMessage());
        }
    }

    /**
     * 启用/停用优惠券模板（管理员）
     */
    @PutMapping("/admin/template/{id}/status")
    public Map<String, Object> updateCouponTemplateStatus(@PathVariable Integer id,
            @RequestParam Integer status) {
        try {
            logger.info("管理员更新优惠券模板状态: {} -> {}", id, status);
            boolean success = couponService.updateCouponTemplateStatus(id, status);
            if (success) {
                return ResultUtil.resultCode(200, "状态更新成功");
            } else {
                return ResultUtil.resultCode(404, "优惠券模板不存在");
            }
        } catch (Exception e) {
            logger.error("更新优惠券模板状态失败", e);
            return ResultUtil.resultCode(500, "更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除优惠券模板（管理员）
     */
    @DeleteMapping("/admin/template/{id}")
    public Map<String, Object> deleteCouponTemplate(@PathVariable Integer id) {
        try {
            logger.info("管理员删除优惠券模板: {}", id);
            boolean success = couponService.deleteCouponTemplate(id);
            if (success) {
                return ResultUtil.resultCode(200, "删除成功");
            } else {
                return ResultUtil.resultCode(404, "优惠券模板不存在");
            }
        } catch (Exception e) {
            logger.error("删除优惠券模板失败", e);
            return ResultUtil.resultCode(500, "删除失败: " + e.getMessage());
        }
    }

    // ==================== 用户接口 ====================

    /**
     * 获取可领取的优惠券模板
     */
    @GetMapping("/templates")
    public Map<String, Object> getAvailableCouponTemplates() {
        try {
            logger.info("用户查询可领取的优惠券模板");
            List<CouponTemplateDto> templates = couponService.getEnabledCouponTemplates();
            return ResultUtil.resultCode(200, "查询成功", templates);
        } catch (Exception e) {
            logger.error("查询可领取优惠券模板失败", e);
            return ResultUtil.resultCode(500, "查询失败: " + e.getMessage());
        }
    }

    /**
     * 用户领取优惠券
     */
    @PostMapping("/claim/{templateId}")
    public Map<String, Object> claimCoupon(@PathVariable Integer templateId) {
        try {
            String account = getCurrentUserAccount();
            logger.info("用户领取优惠券: {} -> {}", account, templateId);

            UserCoupon userCoupon = couponService.claimCoupon(templateId, account);
            return ResultUtil.resultCode(200, "领取成功", userCoupon);

        } catch (Exception e) {
            logger.error("领取优惠券失败", e);
            return ResultUtil.resultCode(500, "领取失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的所有优惠券
     */
    @GetMapping("/my")
    public Map<String, Object> getUserCoupons() {
        try {
            String account = getCurrentUserAccount();
            logger.info("用户查询优惠券: {}", account);

            List<UserCouponDto> coupons = couponService.getUserCoupons(account);
            return ResultUtil.resultCode(200, "查询成功", coupons);

        } catch (Exception e) {
            logger.error("查询用户优惠券失败", e);
            return ResultUtil.resultCode(500, "查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户可用的优惠券
     */
    @GetMapping("/my/available")
    public Map<String, Object> getAvailableUserCoupons() {
        try {
            String account = getCurrentUserAccount();
            logger.info("用户查询可用优惠券: {}", account);

            List<UserCouponDto> coupons = couponService.getAvailableUserCoupons(account);
            return ResultUtil.resultCode(200, "查询成功", coupons);

        } catch (Exception e) {
            logger.error("查询用户可用优惠券失败", e);
            return ResultUtil.resultCode(500, "查询失败: " + e.getMessage());
        }
    }

    /**
     * 计算优惠券折扣
     */
    @PostMapping("/calculate")
    public Map<String, Object> calculateCouponDiscount(@RequestParam String couponCode,
            @RequestParam BigDecimal orderAmount) {
        try {
            String account = getCurrentUserAccount();
            logger.info("计算优惠券折扣: {} -> {} -> {}", account, couponCode, orderAmount);

            CouponCalculationResult result = couponService.calculateCouponDiscount(couponCode, account, orderAmount);
            return ResultUtil.resultCode(200, "计算成功", result);

        } catch (Exception e) {
            logger.error("计算优惠券折扣失败", e);
            return ResultUtil.resultCode(500, "计算失败: " + e.getMessage());
        }
    }

    /**
     * 验证优惠券是否可用
     */
    @GetMapping("/validate")
    public Map<String, Object> validateCoupon(@RequestParam String couponCode,
            @RequestParam BigDecimal orderAmount) {
        try {
            String account = getCurrentUserAccount();
            logger.info("验证优惠券: {} -> {} -> {}", account, couponCode, orderAmount);

            boolean valid = couponService.validateCoupon(couponCode, account, orderAmount);
            return ResultUtil.resultCode(200, "验证成功", valid);

        } catch (Exception e) {
            logger.error("验证优惠券失败", e);
            return ResultUtil.resultCode(500, "验证失败: " + e.getMessage());
        }
    }

    // ==================== 内部接口（供订单系统调用） ====================

    /**
     * 使用优惠券（供订单系统调用）
     */
    @PostMapping("/internal/use")
    public Map<String, Object> useCoupon(@RequestBody CouponUsageRequest request) {
        try {
            logger.info("订单系统使用优惠券: {} -> {}", request.getAccount(), request.getCouponCode());

            boolean success = couponService.useCoupon(request);
            if (success) {
                return ResultUtil.resultCode(200, "优惠券使用成功");
            } else {
                return ResultUtil.resultCode(500, "优惠券使用失败");
            }

        } catch (Exception e) {
            logger.error("使用优惠券失败", e);
            return ResultUtil.resultCode(500, "使用失败: " + e.getMessage());
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 获取当前登录用户账号
     */
    private String getCurrentUserAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new RuntimeException("用户未登录");
    }
}
