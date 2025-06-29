package com.huang.store.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 基础实体类
 * 
 * @author 系统管理员
 * @date 2024-01-01
 * @description 包含所有实体类的公共字段
 */
@Data
public abstract class BaseEntity {
    
    /**
     * 主键ID
     */
    private Integer id;
    
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
}
