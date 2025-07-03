package com.huang.store.mapper;

import com.huang.store.entity.order.UserCoupon;
import com.huang.store.entity.dto.UserCouponDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户优惠券Mapper接口
 * 
 * @author: 黄龙
 * @date: 2024/12/01
 * @description: 用户优惠券数据访问层
 */
@Mapper
public interface UserCouponMapper {

        /**
         * 插入用户优惠券
         */
        @Insert("INSERT INTO user_coupon (coupon_template_id, account, coupon_code, status, " +
                        "receive_time, expire_time, discount_amount, create_time, update_time) " +
                        "VALUES (#{couponTemplateId}, #{account}, #{couponCode}, #{status}, " +
                        "#{receiveTime}, #{expireTime}, #{discountAmount}, #{createTime}, #{updateTime})")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(UserCoupon userCoupon);

        /**
         * 根据ID查询用户优惠券
         */
        @Select("SELECT * FROM user_coupon WHERE id = #{id}")
        UserCoupon selectById(Integer id);

        /**
         * 根据优惠券码查询用户优惠券
         */
        @Select("SELECT * FROM user_coupon WHERE coupon_code = #{couponCode}")
        UserCoupon selectByCouponCode(String couponCode);

        /**
         * 根据订单ID查询用户优惠券
         */
        @Select("SELECT * FROM user_coupon WHERE order_id = #{orderId}")
        UserCoupon selectByOrderId(String orderId);

        /**
         * 查询用户的所有优惠券
         */
        @Select("SELECT uc.*, ct.name as coupon_name, ct.type, ct.discount_value, " +
                        "ct.min_order_amount, ct.max_discount_amount " +
                        "FROM user_coupon uc " +
                        "LEFT JOIN coupon_template ct ON uc.coupon_template_id = ct.id " +
                        "WHERE uc.account = #{account} " +
                        "ORDER BY uc.receive_time DESC")
        @Results({
                        @Result(property = "id", column = "id"),
                        @Result(property = "couponTemplateId", column = "coupon_template_id"),
                        @Result(property = "account", column = "account"),
                        @Result(property = "couponCode", column = "coupon_code"),
                        @Result(property = "status", column = "status"),
                        @Result(property = "receiveTime", column = "receive_time"),
                        @Result(property = "useTime", column = "use_time"),
                        @Result(property = "orderId", column = "order_id"),
                        @Result(property = "expireTime", column = "expire_time"),
                        @Result(property = "discountAmount", column = "discount_amount"),
                        @Result(property = "couponName", column = "coupon_name"),
                        @Result(property = "type", column = "type"),
                        @Result(property = "discountValue", column = "discount_value"),
                        @Result(property = "minOrderAmount", column = "min_order_amount"),
                        @Result(property = "maxDiscountAmount", column = "max_discount_amount")
        })
        List<UserCouponDto> selectByAccount(String account);

        /**
         * 查询用户可用的优惠券
         */
        @Select("SELECT uc.*, ct.name as coupon_name, ct.type, ct.discount_value, " +
                        "ct.min_order_amount, ct.max_discount_amount " +
                        "FROM user_coupon uc " +
                        "LEFT JOIN coupon_template ct ON uc.coupon_template_id = ct.id " +
                        "WHERE uc.account = #{account} AND uc.status = 1 AND uc.expire_time > NOW() " +
                        "AND ct.status = 1 " +
                        "ORDER BY uc.expire_time ASC")
        @Results({
                        @Result(property = "id", column = "id"),
                        @Result(property = "couponTemplateId", column = "coupon_template_id"),
                        @Result(property = "account", column = "account"),
                        @Result(property = "couponCode", column = "coupon_code"),
                        @Result(property = "status", column = "status"),
                        @Result(property = "receiveTime", column = "receive_time"),
                        @Result(property = "useTime", column = "use_time"),
                        @Result(property = "orderId", column = "order_id"),
                        @Result(property = "expireTime", column = "expire_time"),
                        @Result(property = "discountAmount", column = "discount_amount"),
                        @Result(property = "couponName", column = "coupon_name"),
                        @Result(property = "type", column = "type"),
                        @Result(property = "discountValue", column = "discount_value"),
                        @Result(property = "minOrderAmount", column = "min_order_amount"),
                        @Result(property = "maxDiscountAmount", column = "max_discount_amount")
        })
        List<UserCouponDto> selectAvailableByAccount(String account);

        /**
         * 更新优惠券使用状态
         */
        @Update("UPDATE user_coupon SET status = #{status}, use_time = #{useTime}, " +
                        "order_id = #{orderId}, discount_amount = #{discountAmount}, update_time = NOW() " +
                        "WHERE id = #{id}")
        int updateUsageStatus(UserCoupon userCoupon);

        /**
         * 标记过期优惠券
         */
        @Update("UPDATE user_coupon SET status = 3, update_time = NOW() " +
                        "WHERE status = 1 AND expire_time < NOW()")
        int markExpiredCoupons();

        /**
         * 检查优惠券是否可用
         */
        @Select("SELECT uc.*, ct.name as coupon_name, ct.type, ct.discount_value, " +
                        "ct.min_order_amount, ct.max_discount_amount " +
                        "FROM user_coupon uc " +
                        "LEFT JOIN coupon_template ct ON uc.coupon_template_id = ct.id " +
                        "WHERE uc.coupon_code = #{couponCode} AND uc.account = #{account} " +
                        "AND uc.status = 1 AND uc.expire_time > NOW() AND ct.status = 1")
        @Results({
                        @Result(property = "id", column = "id"),
                        @Result(property = "couponTemplateId", column = "coupon_template_id"),
                        @Result(property = "account", column = "account"),
                        @Result(property = "couponCode", column = "coupon_code"),
                        @Result(property = "status", column = "status"),
                        @Result(property = "receiveTime", column = "receive_time"),
                        @Result(property = "useTime", column = "use_time"),
                        @Result(property = "orderId", column = "order_id"),
                        @Result(property = "expireTime", column = "expire_time"),
                        @Result(property = "discountAmount", column = "discount_amount"),
                        @Result(property = "couponName", column = "coupon_name"),
                        @Result(property = "type", column = "type"),
                        @Result(property = "discountValue", column = "discount_value"),
                        @Result(property = "minOrderAmount", column = "min_order_amount"),
                        @Result(property = "maxDiscountAmount", column = "max_discount_amount")
        })
        UserCouponDto selectAvailableByCouponCodeAndAccount(@Param("couponCode") String couponCode,
                        @Param("account") String account);



        /**
         * 统计用户优惠券数量
         */
        @Select("SELECT COUNT(*) FROM user_coupon WHERE account = #{account} AND status = #{status}")
        int countByAccountAndStatus(@Param("account") String account, @Param("status") Integer status);
}
