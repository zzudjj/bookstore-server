package com.huang.store.entity.spike;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.huang.store.entity.book.Book;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀商品实体类
 */
@Data
public class SpikeGoods {
    private Long id;
    private Long activityId;
    private Integer bookId;
    private BigDecimal spikePrice;
    private BigDecimal originalPrice;
    private Integer spikeStock;
    private Integer soldCount;
    private Integer limitPerUser;
    private Integer sortOrder;
    private Integer status; // 0-下架，1-上架
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    // 关联的活动信息
    private SpikeActivity activity;
    
    // 关联的图书信息
    private Book book;

    // 图书封面图片（用于前端显示）
    private String coverImg;

    // 剩余库存（直接返回spikeStock，因为reduceStock已经直接减少了库存）
    public Integer getRemainStock() {
        return spikeStock != null ? spikeStock : 0;
    }

    // 当前库存（前端需要的字段名）
    public Integer getStock() {
        return getRemainStock();
    }
    
    // 总库存（前端需要的字段名）
    public Integer getTotalStock() {
        return this.spikeStock;
    }
    
    // 折扣率
    public String getDiscountRate() {
        if (originalPrice != null && originalPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = spikePrice.divide(originalPrice, 2, BigDecimal.ROUND_HALF_UP);
            return String.format("%.1f折", discount.multiply(new BigDecimal(10)).doubleValue());
        }
        return "";
    }
    
    // 状态描述
    public String getStatusDesc() {
        return status == 1 ? "上架" : "下架";
    }
    
    // 前端需要的字段映射
    public String getName() {
        return book != null ? book.getBookName() : "";
    }
    
    public String getAuthor() {
        return book != null ? book.getAuthor() : "";
    }
    
    public BigDecimal getPrice() {
        return this.originalPrice;
    }
    
    public String getCoverImg() {
        // 优先返回设置的封面图片
        if (coverImg != null && !coverImg.isEmpty()) {
            return coverImg;
        }

        // 如果没有设置，尝试从关联的Book获取
        if (book != null) {
            // 如果Book有coverImg字段，直接返回
            if (book.getCoverImg() != null && !book.getCoverImg().isEmpty()) {
                return book.getCoverImg();
            }
            // 如果Book有ImgSrc列表，返回第一个
            if (book.getImgSrc() != null && !book.getImgSrc().isEmpty()) {
                return book.getImgSrc().get(0);
            }
        }
        return "";
    }
}
