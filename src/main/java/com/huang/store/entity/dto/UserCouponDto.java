package com.huang.store.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 用户优惠券DTO
 * 
 * @author: 黄龙
 * @date: 2024/12/01
 * @description: 用于前后端交互的用户优惠券数据传输对象
 */
@Data
public class UserCouponDto {

    /**
     * 用户优惠券ID
     */
    private Integer id;

    /**
     * 优惠券模板ID
     */
    private Integer couponTemplateId;

    /**
     * 用户账号
     */
    private String account;

    /**
     * 优惠券码
     */
    private String couponCode;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 优惠券类型：1-满减券，2-折扣券
     */
    private Integer type;

    /**
     * 优惠券类型描述
     */
    private String typeDesc;

    /**
     * 折扣值
     */
    private BigDecimal discountValue;

    /**
     * 最低消费金额
     */
    private BigDecimal minOrderAmount;

    /**
     * 最大折扣金额
     */
    private BigDecimal maxDiscountAmount;

    /**
     * 状态：1-未使用，2-已使用，3-已过期
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

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
     * 实际折扣金额
     */
    private BigDecimal discountAmount;

    /**
     * 优惠描述（如：满100减20、8.5折优惠）
     */
    private String discountDesc;

    /**
     * 是否可用
     */
    private Boolean available;

    /**
     * 不可用原因
     */
    private String unavailableReason;
}
