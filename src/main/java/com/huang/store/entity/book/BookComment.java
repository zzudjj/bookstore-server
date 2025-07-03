package com.huang.store.entity.book;

import lombok.Data;
import java.sql.Timestamp;
import java.util.List;

/**
 * 图书评论实体类
 */
@Data
public class BookComment {
    private int id;                 // 评论编号
    private int bookId;             // 图书ID
    private int userId;             // 用户ID
    private Integer parentId;       // 父评论ID（用于二级评论）
    private String content;         // 评论内容
    private int likeCount;          // 点赞数
    private Timestamp createTime;   // 创建时间
    
    // 扩展字段（用于前端显示）
    private String userName;        // 用户名
    private String userImgUrl;      // 用户头像
    private String bookName;        // 图书名称
    private List<BookComment> replies; // 回复列表（用于二级评论）
    private Boolean liked;            // 当前用户是否已点赞（扩展字段）
} 