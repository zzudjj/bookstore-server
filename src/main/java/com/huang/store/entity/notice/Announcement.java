package com.huang.store.entity.notice;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 公告实体
 */
@Data
public class Announcement {
    /** 公告编号 */
    private Integer id;
    /** 公告标题 */
    private String title;
    /** 公告内容 */
    private String content;
    /** 发布人账号 */
    private String author;
    /** 发布时间 */
    private Timestamp publishTime;
    /** 是否展示 */
    private Boolean enable;
} 