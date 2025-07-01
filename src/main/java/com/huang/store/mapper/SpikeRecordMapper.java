package com.huang.store.mapper;

import com.huang.store.entity.spike.SpikeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 秒杀记录Mapper接口
 */
@Mapper
public interface SpikeRecordMapper {
    
    /**
     * 插入秒杀记录
     */
    int insertSpikeRecord(SpikeRecord spikeRecord);
    
    /**
     * 获取用户秒杀记录
     */
    List<SpikeRecord> getUserSpikeRecords(@Param("userAccount") String userAccount,
                                          @Param("offset") int offset,
                                          @Param("pageSize") int pageSize);
    
    /**
     * 获取商品秒杀记录
     */
    List<SpikeRecord> getGoodsSpikeRecords(@Param("spikeGoodsId") Long spikeGoodsId,
                                           @Param("offset") int offset,
                                           @Param("pageSize") int pageSize);
    
    /**
     * 获取用户对指定商品的秒杀次数
     */
    int getUserSpikeCount(@Param("userAccount") String userAccount, @Param("spikeGoodsId") Long spikeGoodsId);
    
    /**
     * 获取秒杀成功率统计
     */
    List<SpikeRecord> getSpikeStatistics(@Param("spikeGoodsId") Long spikeGoodsId);

    /**
     * 检查用户对指定商品的成功购买数量（用于限购检查）
     */
    int getUserSuccessPurchaseCount(@Param("userAccount") String userAccount, @Param("spikeGoodsId") Long spikeGoodsId);
}
