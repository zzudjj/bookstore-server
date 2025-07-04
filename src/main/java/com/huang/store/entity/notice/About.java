package com.huang.store.entity.notice;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 网站介绍实体 (预计只有一条记录)
 */
@Data
public class About {
    /** 主键 (固定 1) */
    private Integer id;
    /** 网站介绍内容 */
    private String content;
    /** 最后更新时间 */
    private Timestamp updateTime;
} 