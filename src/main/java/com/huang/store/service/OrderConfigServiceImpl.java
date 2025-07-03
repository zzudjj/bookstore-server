package com.huang.store.service;

import com.huang.store.entity.order.OrderConfig;
import com.huang.store.mapper.OrderConfigMapper;
import com.huang.store.service.imp.OrderConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单配置Service实现类
 * 
 * @author Augment Agent
 * @date 2025-07-03
 * @description 订单配置业务逻辑层实现
 */
@Service
public class OrderConfigServiceImpl implements OrderConfigService {
    
    @Autowired
    private OrderConfigMapper orderConfigMapper;
    
    @Override
    public String getConfigValue(String configKey) {
        OrderConfig config = orderConfigMapper.getByKey(configKey);
        return config != null ? config.getConfigValue() : null;
    }
    
    @Override
    public Integer getConfigIntValue(String configKey) {
        String value = getConfigValue(configKey);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.err.println("配置值转换为整数失败: " + configKey + " = " + value);
            }
        }
        return null;
    }
    
    @Override
    public Long getConfigLongValue(String configKey) {
        String value = getConfigValue(configKey);
        if (value != null) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                System.err.println("配置值转换为长整数失败: " + configKey + " = " + value);
            }
        }
        return null;
    }
    
    @Override
    public Boolean getConfigBooleanValue(String configKey) {
        String value = getConfigValue(configKey);
        return value != null && ("true".equalsIgnoreCase(value) || "1".equals(value));
    }
    
    @Override
    public List<OrderConfig> getAllConfigs() {
        return orderConfigMapper.getAllConfigs();
    }
    
    @Override
    public Map<String, String> getConfigMap() {
        List<OrderConfig> configs = getAllConfigs();
        Map<String, String> configMap = new HashMap<>();
        for (OrderConfig config : configs) {
            configMap.put(config.getConfigKey(), config.getConfigValue());
        }
        return configMap;
    }
    
    @Override
    public boolean updateConfig(String configKey, String configValue) {
        try {
            int result = orderConfigMapper.updateConfig(configKey, configValue);
            System.out.println("更新配置: " + configKey + " = " + configValue + ", 结果: " + (result > 0 ? "成功" : "失败"));
            return result > 0;
        } catch (Exception e) {
            System.err.println("更新配置失败: " + configKey + " = " + configValue + ", 错误: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean batchUpdateConfigs(Map<String, String> configMap) {
        try {
            int successCount = 0;
            for (Map.Entry<String, String> entry : configMap.entrySet()) {
                if (updateConfig(entry.getKey(), entry.getValue())) {
                    successCount++;
                }
            }
            System.out.println("批量更新配置完成: 成功" + successCount + "个，总共" + configMap.size() + "个");
            return successCount == configMap.size();
        } catch (Exception e) {
            System.err.println("批量更新配置失败: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public Integer getSpikePaymentTimeout() {
        Integer timeout = getConfigIntValue("spike_payment_timeout");
        return timeout != null ? timeout : 30; // 默认30分钟
    }
    
    @Override
    public Integer getNormalPaymentTimeout() {
        Integer timeout = getConfigIntValue("normal_payment_timeout");
        return timeout != null ? timeout : 60; // 默认60分钟
    }
    
    @Override
    public Integer getDeliveryTimeout() {
        Integer timeout = getConfigIntValue("delivery_timeout");
        return timeout != null ? timeout : 7; // 默认7天
    }
    
    @Override
    public Integer getReceiveTimeout() {
        Integer timeout = getConfigIntValue("receive_timeout");
        return timeout != null ? timeout : 7; // 默认7天
    }
}
