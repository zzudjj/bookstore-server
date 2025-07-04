package com.huang.store.entity.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 订单实体类
 *
 * @author: 黄龙
 * @date: 2020/7/28 8:42
 * @description: 订单
 */
@Data
public class Order {
    private int id;// 编号
    private String orderId;// 订单号
    private String account;// 账户
    private int addressId;// 收货地址编号
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp orderTime;//下单时间
    private Timestamp shipTime;//发货时间
    private Timestamp getTime;//收货时间
    private Timestamp evaluateTime;//评价时间
    private Timestamp closeTime;//结束时间
    private Timestamp confirmTime;//自动确认收货的时间 默认14天
    private String orderStatus;//订单状态
    private int logisticsCompany;//物流公司 用id号进行辨识是哪个物流公司,这样物流公司也可以进行管理
    private String logisticsNum;//物流单号
    private boolean beUserDelete;//订单是否被用户删除的标记

    // 新增字段
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp paymentDeadline;//支付截止时间
    private String cancelReason;//取消原因
    private String operator;//最后操作人
    private Integer couponId;// 使用的优惠券ID
    private String couponCode;// 使用的优惠券码

    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
}
