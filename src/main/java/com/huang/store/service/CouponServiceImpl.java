package com.huang.store.service;

import com.huang.store.entity.order.CouponTemplate;
import com.huang.store.entity.order.UserCoupon;
import com.huang.store.entity.dto.*;
import com.huang.store.mapper.CouponTemplateMapper;
import com.huang.store.mapper.UserCouponMapper;
import com.huang.store.service.imp.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 优惠券服务实现类
 * 
 * @author: 黄龙
 * @date: 2024/12/01
 * @description: 优惠券业务逻辑实现
 */
@Service
@Transactional
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponTemplateMapper couponTemplateMapper;

    @Autowired
    private UserCouponMapper userCouponMapper;

    // ==================== 优惠券模板管理 ====================

    @Override
    public CouponTemplate createCouponTemplate(CouponCreateRequest request) {
        CouponTemplate template = new CouponTemplate();
        template.setName(request.getName());
        template.setType(request.getType());
        template.setDiscountValue(request.getDiscountValue());
        template.setMinOrderAmount(request.getMinOrderAmount());
        template.setMaxDiscountAmount(request.getMaxDiscountAmount());
        template.setTotalQuantity(request.getTotalQuantity());
        template.setUsedQuantity(0);
        template.setReceivedQuantity(0);
        template.setPerUserLimit(request.getPerUserLimit());
        template.setValidDays(request.getValidDays());
        template.setStatus(CouponTemplate.CouponStatus.ENABLED.getCode());
        template.setCreateTime(new Timestamp(System.currentTimeMillis()));
        template.setUpdateTime(new Timestamp(System.currentTimeMillis()));

        couponTemplateMapper.insert(template);
        return template;
    }

    @Override
    public List<CouponTemplateDto> getAllCouponTemplates() {
        List<CouponTemplate> templates = couponTemplateMapper.selectAll();
        return templates.stream()
                .map(this::convertToTemplateDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CouponTemplateDto> getEnabledCouponTemplates() {
        List<CouponTemplate> templates = couponTemplateMapper.selectAllEnabled();
        return templates.stream()
                .map(this::convertToTemplateDto)
                .collect(Collectors.toList());
    }

    @Override
    public CouponTemplateDto getCouponTemplateById(Integer id) {
        CouponTemplate template = couponTemplateMapper.selectById(id);
        return template != null ? convertToTemplateDto(template) : null;
    }

    @Override
    public boolean updateCouponTemplate(Integer id, CouponCreateRequest request) {
        CouponTemplate template = couponTemplateMapper.selectById(id);
        if (template == null) {
            return false;
        }

        template.setName(request.getName());
        template.setType(request.getType());
        template.setDiscountValue(request.getDiscountValue());
        template.setMinOrderAmount(request.getMinOrderAmount());
        template.setMaxDiscountAmount(request.getMaxDiscountAmount());
        template.setTotalQuantity(request.getTotalQuantity());
        template.setPerUserLimit(request.getPerUserLimit());
        template.setValidDays(request.getValidDays());
        template.setUpdateTime(new Timestamp(System.currentTimeMillis()));

        return couponTemplateMapper.update(template) > 0;
    }

    @Override
    public boolean updateCouponTemplateStatus(Integer id, Integer status) {
        return couponTemplateMapper.updateStatus(id, status) > 0;
    }

    @Override
    public boolean deleteCouponTemplate(Integer id) {
        // 1. 检查优惠券模板是否存在
        CouponTemplate template = couponTemplateMapper.selectById(id);
        if (template == null) {
            throw new RuntimeException("优惠券模板不存在");
        }

        // 2. 检查删除条件
        int totalUserCoupons = couponTemplateMapper.countUserCoupons(id);
        int unusedUserCoupons = couponTemplateMapper.countUnusedUserCoupons(id);
        int validUserCoupons = couponTemplateMapper.countValidUserCoupons(id);

        // 3. 判断是否可以删除
        if (totalUserCoupons == 0) {
            // 情况1：没有用户领取过，可以直接删除
            return couponTemplateMapper.delete(id) > 0;
        } else if (unusedUserCoupons == 0 && validUserCoupons == 0) {
            // 情况2：所有已领取的优惠券都已使用完毕且已过期，可以删除
            return couponTemplateMapper.delete(id) > 0;
        } else {
            // 情况3：还有未使用或未过期的优惠券，不能删除
            String reason = "";
            if (unusedUserCoupons > 0) {
                reason += String.format("还有%d张未使用的优惠券", unusedUserCoupons);
            }
            if (validUserCoupons > 0) {
                if (!reason.isEmpty())
                    reason += "，";
                reason += String.format("还有%d张未过期的优惠券", validUserCoupons);
            }
            throw new RuntimeException(String.format("无法删除该优惠券模板：%s。请使用停用功能或等待所有优惠券使用完毕后再删除。", reason));
        }
    }

    // ==================== 用户优惠券管理 ====================

    @Override
    public UserCoupon claimCoupon(Integer templateId, String account) {
        // 1. 检查模板是否存在且启用
        CouponTemplate template = couponTemplateMapper.selectById(templateId);
        if (template == null || !template.getStatus().equals(CouponTemplate.CouponStatus.ENABLED.getCode())) {
            throw new RuntimeException("优惠券模板不存在或已停用");
        }

        // 2. 检查库存
        if (!couponTemplateMapper.hasStock(templateId)) {
            throw new RuntimeException("优惠券已领完");
        }

        // 3. 检查用户领取限制
        int userReceivedCount = couponTemplateMapper.getUserReceivedCount(templateId, account);
        if (userReceivedCount >= template.getPerUserLimit()) {
            throw new RuntimeException("已达到领取上限");
        }

        // 4. 生成优惠券
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setCouponTemplateId(templateId);
        userCoupon.setAccount(account);
        userCoupon.setCouponCode(generateCouponCode(templateId, template.getType()));
        userCoupon.setStatus(UserCoupon.UserCouponStatus.UNUSED.getCode());
        userCoupon.setReceiveTime(new Timestamp(System.currentTimeMillis()));
        userCoupon.setExpireTime(
                new Timestamp(System.currentTimeMillis() + template.getValidDays() * 24L * 60 * 60 * 1000));
        userCoupon.setDiscountAmount(BigDecimal.ZERO);
        userCoupon.setCreateTime(new Timestamp(System.currentTimeMillis()));
        userCoupon.setUpdateTime(new Timestamp(System.currentTimeMillis()));

        // 5. 保存用户优惠券
        userCouponMapper.insert(userCoupon);

        // 6. 更新模板已领取数量
        couponTemplateMapper.incrementReceivedQuantity(templateId);

        return userCoupon;
    }

    @Override
    public List<UserCouponDto> getUserCoupons(String account) {
        return userCouponMapper.selectByAccount(account);
    }

    @Override
    public List<UserCouponDto> getAvailableUserCoupons(String account) {
        return userCouponMapper.selectAvailableByAccount(account);
    }

    @Override
    public UserCouponDto getCouponByCouponCode(String couponCode, String account) {
        return userCouponMapper.selectAvailableByCouponCodeAndAccount(couponCode, account);
    }

    // ==================== 优惠券使用 ====================

    @Override
    public CouponCalculationResult calculateCouponDiscount(String couponCode, String account, BigDecimal orderAmount) {
        // 1. 获取优惠券信息
        UserCouponDto couponDto = userCouponMapper.selectAvailableByCouponCodeAndAccount(couponCode, account);
        if (couponDto == null) {
            return CouponCalculationResult.unavailable("优惠券不存在或已失效", orderAmount);
        }

        // 2. 检查最低消费金额
        if (orderAmount.compareTo(couponDto.getMinOrderAmount()) < 0) {
            return CouponCalculationResult.unavailable(
                    String.format("订单金额不满足使用条件，需满%.2f元", couponDto.getMinOrderAmount()),
                    orderAmount);
        }

        // 3. 计算折扣金额
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (couponDto.getType().equals(CouponTemplate.CouponType.FULL_REDUCTION.getCode())) {
            // 满减券：直接减免固定金额
            discountAmount = couponDto.getDiscountValue();
        } else if (couponDto.getType().equals(CouponTemplate.CouponType.DISCOUNT.getCode())) {
            // 折扣券：按百分比计算折扣
            BigDecimal discountRate = BigDecimal.valueOf(100).subtract(couponDto.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            discountAmount = orderAmount.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);

            // 检查最大折扣限制
            if (couponDto.getMaxDiscountAmount() != null &&
                    discountAmount.compareTo(couponDto.getMaxDiscountAmount()) > 0) {
                discountAmount = couponDto.getMaxDiscountAmount();
            }
        }

        // 4. 确保折扣金额不超过订单金额
        if (discountAmount.compareTo(orderAmount) > 0) {
            discountAmount = orderAmount;
        }

        return CouponCalculationResult.available(orderAmount, discountAmount, couponDto);
    }

    @Override
    public boolean useCoupon(CouponUsageRequest request) {
        // 1. 计算折扣
        CouponCalculationResult result = calculateCouponDiscount(
                request.getCouponCode(),
                request.getAccount(),
                request.getOrderAmount());

        if (!result.getAvailable()) {
            throw new RuntimeException(result.getReason());
        }

        // 2. 更新优惠券状态
        UserCoupon userCoupon = userCouponMapper.selectByCouponCode(request.getCouponCode());
        userCoupon.setStatus(UserCoupon.UserCouponStatus.USED.getCode());
        userCoupon.setUseTime(new Timestamp(System.currentTimeMillis()));
        userCoupon.setOrderId(request.getOrderId());
        userCoupon.setDiscountAmount(result.getDiscountAmount());

        int updated = userCouponMapper.updateUsageStatus(userCoupon);

        // 3. 更新模板已使用数量
        if (updated > 0) {
            couponTemplateMapper.incrementUsedQuantity(userCoupon.getCouponTemplateId());
        }

        return updated > 0;
    }

    @Override
    public boolean validateCoupon(String couponCode, String account, BigDecimal orderAmount) {
        CouponCalculationResult result = calculateCouponDiscount(couponCode, account, orderAmount);
        return result.getAvailable();
    }

    // ==================== 工具方法 ====================

    @Override
    public String generateCouponCode(Integer templateId, Integer type) {
        String prefix = type.equals(CouponTemplate.CouponType.FULL_REDUCTION.getCode()) ? "FULL" : "DISC";
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String templateIdStr = String.format("%03d", templateId);
        String random = String.format("%06d", new Random().nextInt(999999));

        return prefix + date + templateIdStr + random;
    }

    @Override
    public void markExpiredCoupons() {
        userCouponMapper.markExpiredCoupons();
    }

    @Override
    public CouponTemplateDto convertToTemplateDto(CouponTemplate template) {
        CouponTemplateDto dto = new CouponTemplateDto();
        dto.setId(template.getId());
        dto.setName(template.getName());
        dto.setType(template.getType());
        dto.setTypeDesc(CouponTemplate.CouponType.fromCode(template.getType()).getDescription());
        dto.setDiscountValue(template.getDiscountValue());
        dto.setMinOrderAmount(template.getMinOrderAmount());
        dto.setMaxDiscountAmount(template.getMaxDiscountAmount());
        dto.setTotalQuantity(template.getTotalQuantity());
        dto.setUsedQuantity(template.getUsedQuantity());
        dto.setReceivedQuantity(template.getReceivedQuantity());
        dto.setRemainingQuantity(template.getTotalQuantity() - template.getReceivedQuantity());
        dto.setPerUserLimit(template.getPerUserLimit());
        dto.setValidDays(template.getValidDays());
        dto.setStatus(template.getStatus());
        dto.setStatusDesc(CouponTemplate.CouponStatus.fromCode(template.getStatus()).getDescription());
        dto.setCreateTime(template.getCreateTime());
        dto.setUpdateTime(template.getUpdateTime());

        // 计算使用率
        if (template.getReceivedQuantity() > 0) {
            BigDecimal usageRate = BigDecimal.valueOf(template.getUsedQuantity())
                    .divide(BigDecimal.valueOf(template.getReceivedQuantity()), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            dto.setUsageRate(usageRate);
        } else {
            dto.setUsageRate(BigDecimal.ZERO);
        }

        // 生成优惠描述
        if (template.getType().equals(CouponTemplate.CouponType.FULL_REDUCTION.getCode())) {
            dto.setDiscountDesc(String.format("满%.0f减%.0f",
                    template.getMinOrderAmount(), template.getDiscountValue()));
        } else {
            dto.setDiscountDesc(String.format("%.1f折优惠",
                    template.getDiscountValue().divide(BigDecimal.valueOf(10), 1, RoundingMode.HALF_UP)));
        }

        return dto;
    }

    @Override
    public UserCouponDto convertToUserCouponDto(UserCoupon userCoupon, CouponTemplate template) {
        UserCouponDto dto = new UserCouponDto();
        dto.setId(userCoupon.getId());
        dto.setCouponTemplateId(userCoupon.getCouponTemplateId());
        dto.setAccount(userCoupon.getAccount());
        dto.setCouponCode(userCoupon.getCouponCode());
        dto.setStatus(userCoupon.getStatus());
        dto.setStatusDesc(UserCoupon.UserCouponStatus.fromCode(userCoupon.getStatus()).getDescription());
        dto.setReceiveTime(userCoupon.getReceiveTime());
        dto.setUseTime(userCoupon.getUseTime());
        dto.setOrderId(userCoupon.getOrderId());
        dto.setExpireTime(userCoupon.getExpireTime());
        dto.setDiscountAmount(userCoupon.getDiscountAmount());

        if (template != null) {
            dto.setCouponName(template.getName());
            dto.setType(template.getType());
            dto.setTypeDesc(CouponTemplate.CouponType.fromCode(template.getType()).getDescription());
            dto.setDiscountValue(template.getDiscountValue());
            dto.setMinOrderAmount(template.getMinOrderAmount());
            dto.setMaxDiscountAmount(template.getMaxDiscountAmount());

            // 生成优惠描述
            if (template.getType().equals(CouponTemplate.CouponType.FULL_REDUCTION.getCode())) {
                dto.setDiscountDesc(String.format("满%.0f减%.0f",
                        template.getMinOrderAmount(), template.getDiscountValue()));
            } else {
                dto.setDiscountDesc(String.format("%.1f折优惠",
                        template.getDiscountValue().divide(BigDecimal.valueOf(10), 1, RoundingMode.HALF_UP)));
            }
        }

        // 判断是否可用
        boolean available = userCoupon.getStatus().equals(UserCoupon.UserCouponStatus.UNUSED.getCode()) &&
                userCoupon.getExpireTime().after(new Timestamp(System.currentTimeMillis()));
        dto.setAvailable(available);

        if (!available) {
            if (userCoupon.getStatus().equals(UserCoupon.UserCouponStatus.USED.getCode())) {
                dto.setUnavailableReason("已使用");
            } else if (userCoupon.getStatus().equals(UserCoupon.UserCouponStatus.EXPIRED.getCode()) ||
                    userCoupon.getExpireTime().before(new Timestamp(System.currentTimeMillis()))) {
                dto.setUnavailableReason("已过期");
            }
        }

        return dto;
    }
}
