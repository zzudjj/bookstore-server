package com.huang.store.entity.order;

import com.huang.store.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 优惠券模板实体类
 *
 * @author: 黄龙
 * @date: 2024/12/01
 * @description: 优惠券模板，管理员创建的优惠券类型
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CouponTemplate extends BaseEntity {

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 优惠券类型：1-满减券，2-折扣券
     */
    private Integer type;

    /**
     * 折扣值（满减券为减免金额，折扣券为折扣百分比，如85表示8.5折）
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
     * 已使用数量
     */
    private Integer usedQuantity;

    /**
     * 已领取数量
     */
    private Integer receivedQuantity;

    /**
     * 每用户限领数量
     */
    private Integer perUserLimit;

    /**
     * 有效天数（从领取日开始计算）
     */
    private Integer validDays;

    /**
     * 状态：0-停用，1-启用
     */
    private Integer status;

    /**
     * 优惠券类型枚举
     */
    public enum CouponType {
        FULL_REDUCTION(1, "满减券"),
        DISCOUNT(2, "折扣券");

        private final Integer code;
        private final String description;

        CouponType(Integer code, String description) {
            this.code = code;
            this.description = description;
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static CouponType fromCode(Integer code) {
            for (CouponType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown coupon type code: " + code);
        }
    }

    /**
     * 优惠券状态枚举
     */
    public enum CouponStatus {
        DISABLED(0, "停用"),
        ENABLED(1, "启用");

        private final Integer code;
        private final String description;

        CouponStatus(Integer code, String description) {
            this.code = code;
            this.description = description;
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static CouponStatus fromCode(Integer code) {
            for (CouponStatus status : values()) {
                if (status.code.equals(code)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown coupon status code: " + code);
        }
    }
}
