package com.huang.store.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 评论请求体
 * 只包含前端需要传递的字段
 */
@Data
public class CommentRequest {
    
    @NotNull(message = "图书ID不能为空")
    private Integer bookId;             // 图书ID
    
    @NotBlank(message = "评论内容不能为空")
    private String content;             // 评论内容
    
    private Integer parentId;           // 父评论ID（可选，用于回复评论）
} 