package com.huang.store.mapper;

import com.huang.store.entity.spike.SpikeGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 秒杀商品Mapper接口
 */
@Mapper
public interface SpikeGoodsMapper {
    
    /**
     * 根据活动ID获取秒杀商品列表（包含图书信息）
     */
    List<SpikeGoods> getSpikeGoodsByActivity(@Param("activityId") Long activityId);
    
    /**
     * 获取秒杀商品详情（包含图书信息）
     */
    SpikeGoods getSpikeGoodsDetail(@Param("id") Long id);
    
    /**
     * 插入秒杀商品
     */
    int insertSpikeGoods(SpikeGoods spikeGoods);
    
    /**
     * 更新秒杀商品
     */
    int updateSpikeGoods(SpikeGoods spikeGoods);
    
    /**
     * 扣减库存（乐观锁）
     */
    int reduceStock(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    /**
     * 恢复库存
     */
    int restoreStock(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    /**
     * 更新已售数量
     */
    int updateSoldCount(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    /**
     * 获取指定活动的商品数量
     */
    int getGoodsCountByActivity(@Param("activityId") Long activityId);
    
    /**
     * 获取总商品数量
     */
    int getTotalGoodsCount();
    
    /**
     * 删除秒杀商品
     */
    int deleteSpikeGoods(@Param("id") Long id);
    
    /**
     * 根据图书ID获取秒杀商品
     */
    List<SpikeGoods> getSpikeGoodsByBookId(@Param("bookId") Integer bookId);

    /**
     * 根据活动ID删除秒杀商品
     */
    int deleteByActivityId(@Param("activityId") Long activityId);

    /**
     * 分页获取活动商品
     */
    List<SpikeGoods> getActivityGoodsPaged(@Param("activityId") Long activityId,
                                           @Param("offset") int offset,
                                           @Param("pageSize") int pageSize);

    /**
     * 获取活动商品总数
     */
    int getActivityGoodsCount(@Param("activityId") Long activityId);

    /**
     * 根据ID获取秒杀商品
     */
    SpikeGoods getSpikeGoodsById(@Param("id") Long id);
}
