package com.huang.store.service.imp;

import com.huang.store.entity.order.OrderConfig;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 订单配置Service接口
 * 
 * @author Augment Agent
 * @date 2025-07-03
 * @description 订单配置业务逻辑层接口
 */
@Service
public interface OrderConfigService {
    
    /**
     * 根据配置键获取配置值
     */
    String getConfigValue(String configKey);
    
    /**
     * 根据配置键获取配置值（整数）
     */
    Integer getConfigIntValue(String configKey);
    
    /**
     * 根据配置键获取配置值（长整数）
     */
    Long getConfigLongValue(String configKey);
    
    /**
     * 根据配置键获取配置值（布尔）
     */
    Boolean getConfigBooleanValue(String configKey);
    
    /**
     * 获取所有配置
     */
    List<OrderConfig> getAllConfigs();
    
    /**
     * 获取配置映射（键值对形式）
     */
    Map<String, String> getConfigMap();
    
    /**
     * 更新单个配置
     */
    boolean updateConfig(String configKey, String configValue);
    
    /**
     * 批量更新配置
     */
    boolean batchUpdateConfigs(Map<String, String> configMap);
    
    /**
     * 获取秒杀订单付款超时时间（分钟）
     */
    Integer getSpikePaymentTimeout();
    
    /**
     * 获取正常订单付款超时时间（分钟）
     */
    Integer getNormalPaymentTimeout();
    
    /**
     * 获取发货超时时间（天）
     */
    Integer getDeliveryTimeout();
    
    /**
     * 获取收货超时时间（天）
     */
    Integer getReceiveTimeout();
}
