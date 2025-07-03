package com.huang.store.entity.order;

import java.math.BigDecimal;

/**
 * @author: 黄龙
 * @date: 2020/7/22 9:37
 * @description: 订单费用
 */
public class Expense {
    private String orderId;// 订单编号
    private double productTotalMoney; // 商品总价
    private double freight;// 运费 默认为0元
    private int coupon;// 优惠券 默认为0元（保留原字段兼容性）
    private Integer couponId;// 使用的优惠券ID
    private BigDecimal couponDiscount;// 优惠券折扣金额
    private double activityDiscount;// 活动优惠 默认为0元
    private double allPrice;// 订单总金额
    private double finallyPrice;// 最终实付总额

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getProductTotalMoney() {
        return productTotalMoney;
    }

    public void setProductTotalMoney(double productTotalMoney) {
        this.productTotalMoney = productTotalMoney;
    }

    public double getFreight() {
        return freight;
    }

    public void setFreight(double freight) {
        this.freight = freight;
    }

    public int getCoupon() {
        return coupon;
    }

    public void setCoupon(int coupon) {
        this.coupon = coupon;
    }

    public double getActivityDiscount() {
        return activityDiscount;
    }

    public void setActivityDiscount(double activityDiscount) {
        this.activityDiscount = activityDiscount;
    }

    public double getAllPrice() {
        return allPrice;
    }

    public void setAllPrice(double allPrice) {
        this.allPrice = allPrice;
    }

    public double getFinallyPrice() {
        return finallyPrice;
    }

    public void setFinallyPrice(double finallyPrice) {
        this.finallyPrice = finallyPrice;
    }

    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    public BigDecimal getCouponDiscount() {
        return couponDiscount;
    }

    public void setCouponDiscount(BigDecimal couponDiscount) {
        this.couponDiscount = couponDiscount;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "orderId='" + orderId + '\'' +
                ", productTotalMoney=" + productTotalMoney +
                ", freight=" + freight +
                ", coupon=" + coupon +
                ", couponId=" + couponId +
                ", couponDiscount=" + couponDiscount +
                ", activityDiscount=" + activityDiscount +
                ", allPrice=" + allPrice +
                ", finallyPrice=" + finallyPrice +
                '}';
    }
}
