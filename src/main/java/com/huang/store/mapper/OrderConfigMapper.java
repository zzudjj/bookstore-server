package com.huang.store.mapper;

import com.huang.store.entity.order.OrderConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单配置Mapper接口
 * 
 * @author Augment Agent
 * @date 2025-07-03
 * @description 订单配置数据访问层
 */
@Mapper
@Repository
public interface OrderConfigMapper {
    
    /**
     * 根据配置键获取配置
     */
    OrderConfig getByKey(@Param("configKey") String configKey);
    
    /**
     * 获取所有配置
     */
    List<OrderConfig> getAllConfigs();
    
    /**
     * 更新配置值
     */
    int updateConfig(@Param("configKey") String configKey, @Param("configValue") String configValue);
    
    /**
     * 插入新配置
     */
    int insertConfig(OrderConfig config);
    
    /**
     * 批量更新配置
     */
    int batchUpdateConfigs(@Param("configs") List<OrderConfig> configs);
    
    /**
     * 根据配置键删除配置
     */
    int deleteByKey(@Param("configKey") String configKey);
}
