package com.huang.store.entity.book;

import lombok.Data;

/**
 * 评论点赞记录实体类
 */
@Data
public class CommentLike {
    private int id;           // 点赞记录编号
    private int commentId;    // 评论ID
    private int userId;       // 用户ID
} 