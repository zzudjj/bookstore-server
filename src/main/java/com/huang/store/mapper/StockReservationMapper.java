package com.huang.store.mapper;

import com.huang.store.entity.order.StockReservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

/**
 * 库存预留Mapper接口
 * 
 * @author Augment Agent
 * @date 2025-07-02
 * @description 库存预留数据访问层
 */
@Mapper
@Repository
public interface StockReservationMapper {
    
    /**
     * 插入库存预留记录
     */
    int insert(StockReservation reservation);
    
    /**
     * 根据ID更新库存预留记录
     */
    int updateById(StockReservation reservation);
    
    /**
     * 根据订单ID查找库存预留记录
     */
    List<StockReservation> findByOrderId(@Param("orderId") String orderId);
    
    /**
     * 根据图书ID查找预留中的库存
     */
    List<StockReservation> findReservingByBookId(@Param("bookId") Integer bookId);
    
    /**
     * 查找已过期的预留记录
     */
    List<StockReservation> findExpiredReservations();
    
    /**
     * 查找指定时间之前过期的预留记录
     */
    List<StockReservation> findExpiredReservationsBefore(@Param("expireTime") Timestamp expireTime);
    
    /**
     * 根据订单ID删除库存预留记录
     */
    int deleteByOrderId(@Param("orderId") String orderId);
    
    /**
     * 根据订单ID更新预留状态
     */
    int updateStatusByOrderId(@Param("orderId") String orderId, @Param("status") Integer status);
    
    /**
     * 获取图书的总预留数量
     */
    Integer getTotalReservedQuantity(@Param("bookId") Integer bookId);
    
    /**
     * 批量更新过期预留记录状态为已释放
     */
    int batchUpdateExpiredToReleased(@Param("expireTime") Timestamp expireTime);
}
