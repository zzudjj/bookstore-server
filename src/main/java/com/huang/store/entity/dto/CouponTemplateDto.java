package com.huang.store.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 优惠券模板DTO
 * 
 * @author: 黄龙
 * @date: 2024/12/01
 * @description: 用于前后端交互的优惠券模板数据传输对象
 */
@Data
public class CouponTemplateDto {

    /**
     * 优惠券模板ID
     */
    private Integer id;

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 优惠券类型：1-满减券，2-折扣券
     */
    private Integer type;

    /**
     * 优惠券类型描述
     */
    private String typeDesc;

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
     * 已使用数量
     */
    private Integer usedQuantity;

    /**
     * 已领取数量
     */
    private Integer receivedQuantity;

    /**
     * 剩余数量
     */
    private Integer remainingQuantity;

    /**
     * 每用户限领数量
     */
    private Integer perUserLimit;

    /**
     * 有效天数
     */
    private Integer validDays;

    /**
     * 状态：0-停用，1-启用
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 使用率
     */
    private BigDecimal usageRate;

    /**
     * 优惠描述（如：满100减20、8.5折优惠）
     */
    private String discountDesc;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp updateTime;
}
