package com.huang.store.entity.order;

import com.huang.store.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 用户优惠券实体类
 *
 * @author: 黄龙
 * @date: 2024/12/01
 * @description: 用户领取的优惠券实例
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserCoupon extends BaseEntity {

    /**
     * 优惠券模板ID
     */
    private Integer couponTemplateId;

    /**
     * 用户账号
     */
    private String account;

    /**
     * 优惠券码（自动生成）
     */
    private String couponCode;

    /**
     * 状态：1-未使用，2-已使用，3-已过期
     */
    private Integer status;

    /**
     * 领取时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp receiveTime;

    /**
     * 使用时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp useTime;

    /**
     * 使用的订单号
     */
    private String orderId;

    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp expireTime;

    /**
     * 实际折扣金额（使用时计算并存储）
     */
    private BigDecimal discountAmount;

    /**
     * 用户优惠券状态枚举
     */
    public enum UserCouponStatus {
        UNUSED(1, "未使用"),
        USED(2, "已使用"),
        EXPIRED(3, "已过期");

        private final Integer code;
        private final String description;

        UserCouponStatus(Integer code, String description) {
            this.code = code;
            this.description = description;
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static UserCouponStatus fromCode(Integer code) {
            for (UserCouponStatus status : values()) {
                if (status.code.equals(code)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown user coupon status code: " + code);
        }
    }
}
