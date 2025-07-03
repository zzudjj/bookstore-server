package com.huang.store.service;

import com.huang.store.entity.book.BookComment;
import java.util.List;
import java.util.Map;

public interface CommentService {
    
    // 评论相关操作
    int addComment(BookComment comment);
    int updateComment(BookComment comment);
    int deleteComment(int id);
    BookComment getComment(int id);
    List<BookComment> getCommentsByBook(int bookId, int page, int pageSize);
    List<BookComment> getCommentsByUser(int userId, int page, int pageSize);
    int getCommentCountByBook(int bookId);
    int getCommentCountByUser(int userId);
    
    // 点赞相关操作
    boolean likeComment(int commentId, int userId);
    boolean unlikeComment(int commentId, int userId);
    boolean hasLiked(int commentId, int userId);
    
    // 获取评论详情（包含用户和图书信息）
    List<BookComment> getCommentDetailsByBook(int bookId, int page, int pageSize);
    BookComment getCommentDetail(int id);
    
    // 获取评论层级结构
    Map<String, Object> getCommentTreeByBook(int bookId, int page, int pageSize);
    Map<String, Object> getCommentTreeByBook(int bookId, int page, int pageSize, Integer currentUserId);
    
    // 管理员相关操作
    List<BookComment> getAllComments(int page, int pageSize);
    int getTotalCommentCount();
} 