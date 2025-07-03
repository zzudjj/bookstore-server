package com.huang.store.entity.order;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Timestamp;

/**
 * 库存预留实体类
 * 
 * @author Augment Agent
 * @date 2025-07-02
 * @description 用于管理订单的库存预留信息
 */
@Data
public class StockReservation {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 图书ID
     */
    private Integer bookId;
    
    /**
     * 订单ID
     */
    private String orderId;
    
    /**
     * 预留数量
     */
    private Integer reservedQuantity;
    
    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp expireTime;
    
    /**
     * 状态：0-预留中，1-已确认，2-已释放
     */
    private Integer status;
    
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
     * 获取状态描述
     */
    public String getStatusDesc() {
        switch (status) {
            case 0: return "预留中";
            case 1: return "已确认";
            case 2: return "已释放";
            default: return "未知";
        }
    }
    
    /**
     * 检查是否已过期（基于创建时间+30分钟）
     */
    public boolean isExpired() {
        if (createTime == null) {
            return false;
        }
        // 创建时间超过30分钟就算过期
        long expireTimeMillis = createTime.getTime() + (30 * 60 * 1000);
        return System.currentTimeMillis() > expireTimeMillis;
    }
}
