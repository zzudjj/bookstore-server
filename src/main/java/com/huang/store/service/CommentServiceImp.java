package com.huang.store.service;

import com.huang.store.entity.book.BookComment;
import com.huang.store.entity.book.CommentLike;
import com.huang.store.entity.user.User;
import com.huang.store.mapper.CommentMapper;
import com.huang.store.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service("commentService")
public class CommentServiceImp implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public int addComment(BookComment comment) {
        // 设置创建时间
        comment.setCreateTime(new Timestamp(System.currentTimeMillis()));
        comment.setLikeCount(0);
        return commentMapper.addComment(comment);
    }

    @Override
    public int updateComment(BookComment comment) {
        return commentMapper.updateComment(comment);
    }

    @Override
    public int deleteComment(int id) {
        return commentMapper.deleteComment(id);
    }

    @Override
    public BookComment getComment(int id) {
        return commentMapper.getComment(id);
    }

    @Override
    public List<BookComment> getCommentsByBook(int bookId, int page, int pageSize) {
        int start = (page - 1) * pageSize;
        return commentMapper.getCommentsByBook(bookId, start, pageSize);
    }

    @Override
    public List<BookComment> getCommentsByUser(int userId, int page, int pageSize) {
        int start = (page - 1) * pageSize;
        return commentMapper.getCommentsByUser(userId, start, pageSize);
    }

    @Override
    public int getCommentCountByBook(int bookId) {
        return commentMapper.getCommentCountByBook(bookId);
    }

    @Override
    public int getCommentCountByUser(int userId) {
        return commentMapper.getCommentCountByUser(userId);
    }

    @Override
    public boolean likeComment(int commentId, int userId) {
        // 检查是否已经点赞
        if (commentMapper.hasLiked(commentId, userId) > 0) {
            return false; // 已经点赞过了
        }
        
        // 添加点赞记录
        CommentLike like = new CommentLike();
        like.setCommentId(commentId);
        like.setUserId(userId);
        
        if (commentMapper.addLike(like) > 0) {
            // 更新评论点赞数
            commentMapper.updateLikeCount(commentId, 1);
            return true;
        }
        return false;
    }

    @Override
    public boolean unlikeComment(int commentId, int userId) {
        // 检查是否已经点赞
        if (commentMapper.hasLiked(commentId, userId) == 0) {
            return false; // 没有点赞过
        }
        
        // 删除点赞记录
        if (commentMapper.deleteLike(commentId, userId) > 0) {
            // 更新评论点赞数
            commentMapper.updateLikeCount(commentId, -1);
            return true;
        }
        return false;
    }

    @Override
    public boolean hasLiked(int commentId, int userId) {
        return commentMapper.hasLiked(commentId, userId) > 0;
    }

    @Override
    public List<BookComment> getCommentDetailsByBook(int bookId, int page, int pageSize) {
        int start = (page - 1) * pageSize;
        return commentMapper.getCommentDetailsByBook(bookId, start, pageSize);
    }

    @Override
    public BookComment getCommentDetail(int id) {
        return commentMapper.getCommentDetail(id);
    }

    @Override
    public Map<String, Object> getCommentTreeByBook(int bookId, int page, int pageSize) {
        return getCommentTreeByBook(bookId, page, pageSize, null);
    }

    @Override
    public Map<String, Object> getCommentTreeByBook(int bookId, int page, int pageSize, Integer currentUserId) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取一级评论
        List<BookComment> firstLevelComments = getCommentDetailsByBook(bookId, page, pageSize);
        
        // 为每个一级评论获取回复，并补充用户名和liked
        for (BookComment comment : firstLevelComments) {
            // 设置一级评论用户名
            User user = userMapper.getUserById(comment.getUserId());
            comment.setUserName(user != null ? user.getName() : null);
            // 设置liked
            if (currentUserId != null) {
                comment.setLiked(commentMapper.hasLiked(comment.getId(), currentUserId) > 0);
            } else {
                comment.setLiked(false);
            }
            List<BookComment> replies = commentMapper.getRepliesByParent(comment.getId());
            // 为每个二级评论设置用户名、头像和liked
            for (BookComment reply : replies) {
                User replyUser = userMapper.getUserById(reply.getUserId());
                reply.setUserName(replyUser != null ? replyUser.getName() : null);
                reply.setUserImgUrl(replyUser != null ? replyUser.getImgUrl() : null);
                if (currentUserId != null) {
                    reply.setLiked(commentMapper.hasLiked(reply.getId(), currentUserId) > 0);
                } else {
                    reply.setLiked(false);
                }
            }
            comment.setReplies(replies);
        }
        
        result.put("comments", firstLevelComments);
        result.put("total", getCommentCountByBook(bookId));
        result.put("page", page);
        result.put("pageSize", pageSize);
        
        return result;
    }

    @Override
    public List<BookComment> getAllComments(int page, int pageSize) {
        int start = (page - 1) * pageSize;
        return commentMapper.getAllComments(start, pageSize);
    }

    @Override
    public int getTotalCommentCount() {
        return commentMapper.getTotalCommentCount();
    }
} 