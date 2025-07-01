package com.huang.store.entity.spike;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 秒杀记录实体类
 */
@Data
public class SpikeRecord {
    private Long id;
    private Long spikeGoodsId;
    private String userAccount;
    private Integer quantity; // 购买数量

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime spikeTime;
    
    private Integer result; // 0-失败，1-成功
    private String failReason;
    private String ipAddress;
    private String userAgent;
    
    // 关联的秒杀商品信息
    private SpikeGoods spikeGoods;
    
    // 结果描述
    public String getResultDesc() {
        return result == 1 ? "成功" : "失败";
    }
}
