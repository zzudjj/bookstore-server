package com.huang.store.mapper;

import com.huang.store.entity.order.CouponTemplate;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 优惠券模板Mapper接口
 * 
 * @author: 黄龙
 * @date: 2024/12/01
 * @description: 优惠券模板数据访问层
 */
@Mapper
public interface CouponTemplateMapper {

        /**
         * 插入优惠券模板
         */
        @Insert("INSERT INTO coupon_template (name, type, discount_value, min_order_amount, " +
                        "max_discount_amount, total_quantity, used_quantity, received_quantity, per_user_limit, " +
                        "valid_days, status, create_time, update_time) " +
                        "VALUES (#{name}, #{type}, #{discountValue}, #{minOrderAmount}, " +
                        "#{maxDiscountAmount}, #{totalQuantity}, #{usedQuantity}, #{receivedQuantity}, " +
                        "#{perUserLimit}, #{validDays}, #{status}, #{createTime}, #{updateTime})")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(CouponTemplate couponTemplate);

        /**
         * 根据ID查询优惠券模板
         */
        @Select("SELECT * FROM coupon_template WHERE id = #{id}")
        CouponTemplate selectById(Integer id);

        /**
         * 查询所有启用的优惠券模板
         */
        @Select("SELECT * FROM coupon_template WHERE status = 1 ORDER BY create_time DESC")
        List<CouponTemplate> selectAllEnabled();

        /**
         * 查询所有优惠券模板（管理员用）
         */
        @Select("SELECT * FROM coupon_template ORDER BY create_time DESC")
        List<CouponTemplate> selectAll();

        /**
         * 根据类型查询优惠券模板
         */
        @Select("SELECT * FROM coupon_template WHERE type = #{type} AND status = 1 ORDER BY create_time DESC")
        List<CouponTemplate> selectByType(Integer type);

        /**
         * 更新优惠券模板
         */
        @Update("UPDATE coupon_template SET name = #{name}, " +
                        "type = #{type}, discount_value = #{discountValue}, min_order_amount = #{minOrderAmount}, " +
                        "max_discount_amount = #{maxDiscountAmount}, total_quantity = #{totalQuantity}, " +
                        "per_user_limit = #{perUserLimit}, valid_days = #{validDays}, status = #{status}, " +
                        "update_time = #{updateTime} WHERE id = #{id}")
        int update(CouponTemplate couponTemplate);

        /**
         * 更新已领取数量
         */
        @Update("UPDATE coupon_template SET received_quantity = received_quantity + 1, " +
                        "update_time = NOW() WHERE id = #{id}")
        int incrementReceivedQuantity(Integer id);

        /**
         * 更新已使用数量
         */
        @Update("UPDATE coupon_template SET used_quantity = used_quantity + 1, " +
                        "update_time = NOW() WHERE id = #{id}")
        int incrementUsedQuantity(Integer id);

        /**
         * 检查优惠券是否还有库存
         */
        @Select("SELECT (total_quantity - received_quantity) > 0 FROM coupon_template WHERE id = #{id}")
        boolean hasStock(Integer id);

        /**
         * 获取用户已领取某模板的数量
         */
        @Select("SELECT COUNT(*) FROM user_coupon WHERE coupon_template_id = #{templateId} AND account = #{account}")
        int getUserReceivedCount(@Param("templateId") Integer templateId, @Param("account") String account);

        /**
         * 检查优惠券模板是否有用户已领取
         */
        @Select("SELECT COUNT(*) FROM user_coupon WHERE coupon_template_id = #{templateId}")
        int countUserCoupons(@Param("templateId") Integer templateId);

        /**
         * 检查优惠券模板是否有未使用的用户优惠券
         */
        @Select("SELECT COUNT(*) FROM user_coupon WHERE coupon_template_id = #{templateId} AND status = 1")
        int countUnusedUserCoupons(@Param("templateId") Integer templateId);

        /**
         * 检查优惠券模板是否有未过期的用户优惠券
         */
        @Select("SELECT COUNT(*) FROM user_coupon WHERE coupon_template_id = #{templateId} AND expire_time > NOW()")
        int countValidUserCoupons(@Param("templateId") Integer templateId);

        /**
         * 删除优惠券模板（真删除）
         */
        @Delete("DELETE FROM coupon_template WHERE id = #{id}")
        int delete(Integer id);

        /**
         * 启用/停用优惠券模板
         */
        @Update("UPDATE coupon_template SET status = #{status}, update_time = NOW() WHERE id = #{id}")
        int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
}
