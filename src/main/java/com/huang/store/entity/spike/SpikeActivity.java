package com.huang.store.entity.spike;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 秒杀活动实体类
 */
@Data
public class SpikeActivity {
    private Long id;
    private String activityName;
    private String activityDesc;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private Integer status; // 0-未开始，1-进行中，2-已结束，3-已取消

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    private String createdBy;
    
    // 关联的秒杀商品列表
    private List<SpikeGoods> spikeGoodsList;
    
    // 状态描述
    public String getStatusDesc() {
        switch (status) {
            case 0: return "未开始";
            case 1: return "进行中";
            case 2: return "已结束";
            case 3: return "已取消";
            default: return "未知";
        }
    }
    
    // 前端需要的字段映射
    public String getSpikeName() {
        return this.activityName;
    }
    
    public void setSpikeName(String spikeName) {
        this.activityName = spikeName;
    }
    
    // 获取开始时间字符串（HH:mm格式）
    public String getStartTimeStr() {
        if (startTime != null) {
            return String.format("%02d:%02d", startTime.getHour(), startTime.getMinute());
        }
        return "";
    }
    
    // 获取状态字符串（前端需要的格式）
    public String getStatusStr() {
        switch (status) {
            case 0: return "upcoming";
            case 1: return "ongoing";
            case 2: return "ended";
            case 3: return "cancelled";
            default: return "unknown";
        }
    }
}
