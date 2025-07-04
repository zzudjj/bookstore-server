package com.huang.store.controller;

import com.huang.store.dto.request.CommentRequest;
import com.huang.store.entity.book.BookComment;
import com.huang.store.entity.user.User;
import com.huang.store.service.CommentService;
import com.huang.store.service.imp.UserService;
import com.huang.store.util.JwtTokenUtil;
import com.huang.store.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@ResponseBody
@RequestMapping(value = "/comment")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;

    @Autowired
    @Qualifier("firstUser")
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 添加评论
     * @param commentRequest 评论请求体
     * @param request HTTP请求
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> addComment(@RequestBody CommentRequest commentRequest, HttpServletRequest request) {
        try {
            // 从Token中获取用户信息
            String userAccount = getUserAccountFromToken(request);
            if (userAccount == null) {
                return ResultUtil.resultCode(401, "用户未登录");
            }
            
            // 根据账号获取用户ID
            User user = userService.getUser(userAccount);
            if (user == null) {
                return ResultUtil.resultCode(404, "用户不存在");
            }
            
            // 将请求体转换为实体类
            BookComment comment = new BookComment();
            comment.setBookId(commentRequest.getBookId());
            comment.setContent(commentRequest.getContent());
            comment.setParentId(commentRequest.getParentId());
            comment.setUserId(user.getId());
            
            logger.info("添加评论: bookId={}, userId={}, userAccount={}", comment.getBookId(), comment.getUserId(), userAccount);
            
            int result = commentService.addComment(comment);
            if (result > 0) {
                return ResultUtil.resultCode(200, "评论添加成功");
            } else {
                return ResultUtil.resultCode(500, "评论添加失败");
            }
        } catch (Exception e) {
            logger.error("添加评论失败", e);
            return ResultUtil.resultCode(500, "评论添加失败: " + e.getMessage());
        }
    }

    /**
     * 获取图书评论列表
     * @param bookId 图书ID
     * @param page 页码
     * @param pageSize 每页大小
     * @param request HTTP请求
     * @return
     */
    @GetMapping("/getByBook")
    public Map<String, Object> getCommentsByBook(@RequestParam int bookId,
                                                 @RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int pageSize,
                                                 HttpServletRequest request) {
        logger.info("获取图书评论: bookId={}, page={}, pageSize={}", bookId, page, pageSize);
        try {
            // 获取当前用户ID（可选）
            String userAccount = getUserAccountFromToken(request);
            Integer userId = null;
            if (userAccount != null) {
                User user = userService.getUser(userAccount);
                if (user != null) {
                    userId = user.getId();
                }
            }
            Map<String, Object> result = commentService.getCommentTreeByBook(bookId, page, pageSize, userId);
            return ResultUtil.resultSuccess(result);
        } catch (Exception e) {
            logger.error("获取图书评论失败", e);
            return ResultUtil.resultCode(500, "获取评论失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户评论列表
     * @param page 页码
     * @param pageSize 每页大小
     * @param keyword 搜索关键词
     * @param request HTTP请求
     * @return
     */
    @GetMapping("/getMyComments")
    public Map<String, Object> getMyComments(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int pageSize,
                                             @RequestParam(required = false) String keyword,
                                             HttpServletRequest request) {
        try {
            // 从Token中获取用户信息
            String userAccount = getUserAccountFromToken(request);
            if (userAccount == null) {
                return ResultUtil.resultCode(401, "用户未登录");
            }
            
            // 根据账号获取用户ID
            User user = userService.getUser(userAccount);
            if (user == null) {
                return ResultUtil.resultCode(404, "用户不存在");
            }
            
            logger.info("获取用户评论: userId={}, userAccount={}, page={}, pageSize={}, keyword={}", user.getId(), userAccount, page, pageSize, keyword);

            Map<String, Object> result = new java.util.HashMap<>();

            // 根据是否有搜索关键词选择不同的查询方法
            if (keyword != null && !keyword.trim().isEmpty()) {
                result.put("comments", commentService.searchCommentsByUser(user.getId(), keyword.trim(), page, pageSize));
                result.put("total", commentService.getSearchCommentCountByUser(user.getId(), keyword.trim()));
            } else {
                result.put("comments", commentService.getCommentsByUser(user.getId(), page, pageSize));
                result.put("total", commentService.getCommentCountByUser(user.getId()));
            }

            result.put("page", page);
            result.put("pageSize", pageSize);
            result.put("keyword", keyword);
            return ResultUtil.resultSuccess(result);
        } catch (Exception e) {
            logger.error("获取用户评论失败", e);
            return ResultUtil.resultCode(500, "获取评论失败: " + e.getMessage());
        }
    }

    /**
     * 点赞评论
     * @param commentId 评论ID
     * @param request HTTP请求
     * @return
     */
    @PostMapping("/like")
    public Map<String, Object> likeComment(@RequestParam int commentId,
                                          HttpServletRequest request) {
        try {
            // 从Token中获取用户信息
            String userAccount = getUserAccountFromToken(request);
            if (userAccount == null) {
                return ResultUtil.resultCode(401, "用户未登录");
            }
            
            // 根据账号获取用户ID
            User user = userService.getUser(userAccount);
            if (user == null) {
                return ResultUtil.resultCode(404, "用户不存在");
            }
            
            logger.info("点赞评论: commentId={}, userId={}, userAccount={}", commentId, user.getId(), userAccount);
            
            boolean result = commentService.likeComment(commentId, user.getId());
            if (result) {
                return ResultUtil.resultCode(200, "点赞成功");
            } else {
                return ResultUtil.resultCode(400, "已经点赞过了");
            }
        } catch (Exception e) {
            logger.error("点赞评论失败", e);
            return ResultUtil.resultCode(500, "点赞失败: " + e.getMessage());
        }
    }

    /**
     * 取消点赞
     * @param commentId 评论ID
     * @param request HTTP请求
     * @return
     */
    @PostMapping("/unlike")
    public Map<String, Object> unlikeComment(@RequestParam int commentId,
                                            HttpServletRequest request) {
        try {
            // 从Token中获取用户信息
            String userAccount = getUserAccountFromToken(request);
            if (userAccount == null) {
                return ResultUtil.resultCode(401, "用户未登录");
            }
            
            // 根据账号获取用户ID
            User user = userService.getUser(userAccount);
            if (user == null) {
                return ResultUtil.resultCode(404, "用户不存在");
            }
            
            logger.info("取消点赞: commentId={}, userId={}, userAccount={}", commentId, user.getId(), userAccount);
            
            boolean result = commentService.unlikeComment(commentId, user.getId());
            if (result) {
                return ResultUtil.resultCode(200, "取消点赞成功");
            } else {
                return ResultUtil.resultCode(400, "没有点赞过");
            }
        } catch (Exception e) {
            logger.error("取消点赞失败", e);
            return ResultUtil.resultCode(500, "取消点赞失败: " + e.getMessage());
        }
    }

    /**
     * 检查是否已点赞
     * @param commentId 评论ID
     * @param request HTTP请求
     * @return
     */
    @GetMapping("/hasLiked")
    public Map<String, Object> hasLiked(@RequestParam int commentId,
                                       HttpServletRequest request) {
        try {
            // 从Token中获取用户信息
            String userAccount = getUserAccountFromToken(request);
            if (userAccount == null) {
                return ResultUtil.resultCode(401, "用户未登录");
            }
            
            // 根据账号获取用户ID
            User user = userService.getUser(userAccount);
            if (user == null) {
                return ResultUtil.resultCode(404, "用户不存在");
            }
            
            boolean hasLiked = commentService.hasLiked(commentId, user.getId());
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("hasLiked", hasLiked);
            return ResultUtil.resultSuccess(result);
        } catch (Exception e) {
            logger.error("检查点赞状态失败", e);
            return ResultUtil.resultCode(500, "检查失败: " + e.getMessage());
        }
    }

    /**
     * 删除评论
     * @param id 评论ID
     * @return
     */
    @PostMapping("/delete")
    public Map<String, Object> deleteComment(@RequestParam int id) {
        logger.info("删除评论: id={}", id);
        try {
            int result = commentService.deleteComment(id);
            if (result > 0) {
                return ResultUtil.resultCode(200, "评论删除成功");
            } else {
                return ResultUtil.resultCode(500, "评论删除失败");
            }
        } catch (Exception e) {
            logger.error("删除评论失败", e);
            return ResultUtil.resultCode(500, "删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取评论详情
     * @param id 评论ID
     * @return
     */
    @GetMapping("/getDetail")
    public Map<String, Object> getCommentDetail(@RequestParam int id) {
        logger.info("获取评论详情: id={}", id);
        try {
            BookComment comment = commentService.getCommentDetail(id);
            if (comment != null) {
                Map<String, Object> result = new java.util.HashMap<>();
                result.put("comment", comment);
                return ResultUtil.resultSuccess(result);
            } else {
                return ResultUtil.resultCode(404, "评论不存在");
            }
        } catch (Exception e) {
            logger.error("获取评论详情失败", e);
            return ResultUtil.resultCode(500, "获取失败: " + e.getMessage());
        }
    }

    /**
     * 管理员获取所有评论列表（需要管理员权限）
     * @param page 页码
     * @param pageSize 每页大小
     * @param keyword 搜索关键词
     * @param request HTTP请求
     * @return
     */
    @GetMapping("/admin/getAllComments")
    public Map<String, Object> getAllComments(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int pageSize,
                                              @RequestParam(required = false) String keyword,
                                              HttpServletRequest request) {
        try {
            // 从Token中获取用户信息
            String userAccount = getUserAccountFromToken(request);
            if (userAccount == null) {
                return ResultUtil.resultCode(401, "用户未登录");
            }
            
            // 根据账号获取用户信息
            User user = userService.getUser(userAccount);
            if (user == null) {
                return ResultUtil.resultCode(404, "用户不存在");
            }
            
            // 检查管理员权限
            if (!user.isManage()) {
                logger.warn("非管理员用户尝试访问管理员功能: userAccount={}", userAccount);
                return ResultUtil.resultCode(403, "权限不足，需要管理员权限");
            }
            
            logger.info("管理员获取所有评论: userAccount={}, page={}, pageSize={}, keyword={}", userAccount, page, pageSize, keyword);

            Map<String, Object> result = new java.util.HashMap<>();

            // 根据是否有搜索关键词选择不同的查询方法
            if (keyword != null && !keyword.trim().isEmpty()) {
                result.put("comments", commentService.searchAllComments(keyword.trim(), page, pageSize));
                result.put("total", commentService.getSearchCommentCount(keyword.trim()));
            } else {
                result.put("comments", commentService.getAllComments(page, pageSize));
                result.put("total", commentService.getTotalCommentCount());
            }

            result.put("page", page);
            result.put("pageSize", pageSize);
            result.put("keyword", keyword);
            return ResultUtil.resultSuccess(result);
        } catch (Exception e) {
            logger.error("获取所有评论失败", e);
            return ResultUtil.resultCode(500, "获取评论失败: " + e.getMessage());
        }
    }

    /**
     * 管理员删除评论（需要管理员权限）
     * @param id 评论ID
     * @param request HTTP请求
     * @return
     */
    @PostMapping("/admin/deleteComment")
    public Map<String, Object> adminDeleteComment(@RequestParam int id, HttpServletRequest request) {
        try {
            // 从Token中获取用户信息
            String userAccount = getUserAccountFromToken(request);
            if (userAccount == null) {
                return ResultUtil.resultCode(401, "用户未登录");
            }
            
            // 根据账号获取用户信息
            User user = userService.getUser(userAccount);
            if (user == null) {
                return ResultUtil.resultCode(404, "用户不存在");
            }
            
            // 检查管理员权限
            if (!user.isManage()) {
                logger.warn("非管理员用户尝试删除评论: userAccount={}, commentId={}", userAccount, id);
                return ResultUtil.resultCode(403, "权限不足，需要管理员权限");
            }
            
            logger.info("管理员删除评论: userAccount={}, commentId={}", userAccount, id);
            
            int result = commentService.deleteComment(id);
            if (result > 0) {
                return ResultUtil.resultCode(200, "评论删除成功");
            } else {
                return ResultUtil.resultCode(500, "评论删除失败");
            }
        } catch (Exception e) {
            logger.error("管理员删除评论失败", e);
            return ResultUtil.resultCode(500, "删除失败: " + e.getMessage());
        }
    }

    /**
     * 从Token中获取用户账号
     */
    private String getUserAccountFromToken(HttpServletRequest request) {
        try {
            // 方法1：从SecurityContext中获取（推荐）
            org.springframework.security.core.Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getName())) {
                return authentication.getName();
            }

            // 方法2：直接从Authorization header获取token（兼容现有系统）
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && !authHeader.isEmpty()) {
                return jwtTokenUtil.getUserNameFromToken(authHeader);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
} 