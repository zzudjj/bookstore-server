package com.huang.store.mapper;

import com.huang.store.entity.book.BookComment;
import com.huang.store.entity.book.CommentLike;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentMapper {
    
    // 评论相关操作
    int addComment(BookComment comment);
    int updateComment(BookComment comment);
    int deleteComment(int id);
    BookComment getComment(int id);
    List<BookComment> getCommentsByBook(int bookId, int page, int pageSize);
    List<BookComment> getCommentsByUser(int userId, int page, int pageSize);
    List<BookComment> getRepliesByParent(int parentId);
    int getCommentCountByBook(int bookId);
    int getCommentCountByUser(int userId);
    
    // 点赞相关操作
    int addLike(CommentLike like);
    int deleteLike(int commentId, int userId);
    int hasLiked(int commentId, int userId);
    int updateLikeCount(int commentId, int delta);
    
    // 获取评论详情（包含用户和图书信息）
    List<BookComment> getCommentDetailsByBook(int bookId, int page, int pageSize);
    BookComment getCommentDetail(int id);
    
    // 管理员相关操作
    List<BookComment> getAllComments(int page, int pageSize);
    List<BookComment> searchAllComments(String keyword, int page, int pageSize);
    int getTotalCommentCount();
    int getSearchCommentCount(String keyword);

    // 用户搜索相关操作
    List<BookComment> searchCommentsByUser(int userId, String keyword, int page, int pageSize);
    int getSearchCommentCountByUser(int userId, String keyword);
} 