package com.huang.store.entity.order;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Timestamp;

/**
 * 订单配置实体类
 * 
 * @author Augment Agent
 * @date 2025-07-03
 * @description 用于管理订单相关的配置信息
 */
@Data
public class OrderConfig {
    
    /**
     * 主键ID
     */
    private Integer id;
    
    /**
     * 配置键
     */
    private String configKey;
    
    /**
     * 配置值
     */
    private String configValue;
    
    /**
     * 配置描述
     */
    private String description;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp createTime;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp updateTime;
    
    /**
     * 获取配置值的整数形式
     */
    public Integer getIntValue() {
        try {
            return Integer.parseInt(configValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * 获取配置值的长整数形式
     */
    public Long getLongValue() {
        try {
            return Long.parseLong(configValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * 获取配置值的布尔形式
     */
    public Boolean getBooleanValue() {
        return "true".equalsIgnoreCase(configValue) || "1".equals(configValue);
    }
}
