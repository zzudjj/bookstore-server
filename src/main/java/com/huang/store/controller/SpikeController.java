package com.huang.store.controller;

import com.huang.store.entity.spike.SpikeActivity;
import com.huang.store.entity.spike.SpikeGoods;
import com.huang.store.service.SpikeService;
import com.huang.store.util.ResultUtil;
import com.huang.store.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 秒杀控制器
 * 处理秒杀相关的HTTP请求
 */
@RestController
@RequestMapping("/api/spike")
@CrossOrigin
public class SpikeController {

    @Autowired
    private SpikeService spikeService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 获取秒杀活动列表（前端首页用）
     * 返回格式适配前端Spike.vue的数据结构
     */
    @GetMapping("/list")
    public Map<String, Object> getSpikeList() {
        try {
            List<SpikeActivity> activities = spikeService.getSpikeListForFrontend();
            return ResultUtil.resultCode(200, "获取成功", activities);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.resultCode(500, "获取失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前进行中的秒杀活动列表
     */
    @GetMapping("/activities")
    public Map<String, Object> getActiveActivities() {
        try {
            List<SpikeActivity> activities = spikeService.getActiveActivities();
            return ResultUtil.resultCode(200, "获取成功", activities);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.resultCode(500, "获取失败：" + e.getMessage());
        }
    }

    /**
     * 获取指定活动的秒杀商品列表
     */
    @GetMapping("/goods")
    public Map<String, Object> getSpikeGoods(@RequestParam Long activityId) {
        try {
            if (activityId == null) {
                return ResultUtil.resultCode(400, "活动ID不能为空");
            }
            
            List<SpikeGoods> goodsList = spikeService.getSpikeGoodsByActivity(activityId);
            return ResultUtil.resultCode(200, "获取成功", goodsList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.resultCode(500, "获取失败：" + e.getMessage());
        }
    }

    /**
     * 获取秒杀商品详情
     */
    @GetMapping("/goods/{goodsId}")
    public Map<String, Object> getSpikeGoodsDetail(@PathVariable Long goodsId) {
        try {
            SpikeGoods spikeGoods = spikeService.getSpikeGoodsDetail(goodsId);
            if (spikeGoods == null) {
                return ResultUtil.resultCode(404, "商品不存在");
            }
            return ResultUtil.resultCode(200, "获取成功", spikeGoods);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.resultCode(500, "获取失败：" + e.getMessage());
        }
    }

    /**
     * 执行秒杀
     */
    @PostMapping("/doSpike")
    public Map<String, Object> doSpike(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            // 获取用户信息
            String userAccount = getUserAccountFromToken(request);
            if (userAccount == null) {
                return ResultUtil.resultCode(401, "请先登录");
            }

            // 获取参数
            Long spikeGoodsId = Long.valueOf(params.get("spikeGoodsId").toString());
            Integer quantity = Integer.valueOf(params.getOrDefault("quantity", 1).toString());

            // 获取客户端IP
            String clientIp = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");

            // 执行秒杀
            Map<String, Object> result = spikeService.doSpike(spikeGoodsId, userAccount, quantity, clientIp, userAgent);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.resultCode(500, "秒杀失败：" + e.getMessage());
        }
    }

    /**
     * 根据图书ID执行秒杀（适配前端传递book.id的情况）
     */
    @PostMapping("/spikeBook")
    public Map<String, Object> spikeBook(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            // 获取用户信息
            String userAccount = getUserAccountFromToken(request);
            if (userAccount == null) {
                return ResultUtil.resultCode(401, "请先登录");
            }

            // 获取参数
            Integer bookId = Integer.valueOf(params.get("id").toString());
            Integer quantity = Integer.valueOf(params.getOrDefault("quantity", 1).toString());

            // 获取客户端IP
            String clientIp = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");

            // 根据图书ID执行秒杀
            Map<String, Object> result = spikeService.spikeBookById(bookId, userAccount, quantity, clientIp, userAgent);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.resultCode(500, "秒杀失败：" + e.getMessage());
        }
    }

    // 注意：获取用户秒杀订单列表功能已移除，现在使用统一订单系统查询用户订单

    // 注意：获取秒杀订单详情功能已移除，现在使用统一订单系统查询订单详情

    // 注意：取消秒杀订单功能已移除，现在使用统一订单系统进行订单取消

    // ========================================
    // 管理员接口
    // ========================================

    /**
     * 创建秒杀活动
     */
    @PostMapping("/admin/activities")
    public Map<String, Object> createActivity(@RequestBody SpikeActivity activity, HttpServletRequest request) {
        try {
            String userAccount = getUserAccountFromToken(request);
            if (userAccount == null) {
                return ResultUtil.resultCode(401, "请先登录");
            }

            activity.setCreatedBy(userAccount);
            SpikeActivity result = spikeService.createActivity(activity);
            return ResultUtil.resultCode(200, "创建成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.resultCode(500, "创建失败：" + e.getMessage());
        }
    }

    /**
     * 获取单个活动详情
     */
    @GetMapping("/admin/activities/{activityId}")
    public Map<String, Object> getActivityDetail(@PathVariable Long activityId) {
        try {
            SpikeActivity activity = spikeService.getActivityById(activityId);
            if (activity == null) {
                return ResultUtil.resultCode(404, "活动不存在");
            }
            return ResultUtil.resultCode(200, "获取成功", activity);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.resultCode(500, "获取失败：" + e.getMessage());
        }
    }

    /**
     * 更新秒杀活动
     */
    @PutMapping("/admin/activities/{activityId}")
    public Map<String, Object> updateActivity(@PathVariable Long activityId,
                                               @RequestBody SpikeActivity activity,
                                               HttpServletRequest request) {
        try {
            String userAccount = getUserAccountFromToken(request);
            if (userAccount == null) {
                return ResultUtil.resultCode(401, "请先登录");
            }

            activity.setId(activityId);
            SpikeActivity result = spikeService.updateActivity(activity);
            return ResultUtil.resultCode(200, "更新成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.resultCode(500, "更新失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有秒杀活动（管理员）
     */
    @GetMapping("/admin/activities")
    public Map<String, Object> getAllActivities(@RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                                 @RequestParam(required = false) String activityName,
                                                 @RequestParam(required = false) Integer status,
                                                 @RequestParam(required = false) String startTime,
                                                 @RequestParam(required = false) String endTime) {
        try {
            Map<String, Object> searchParams = new HashMap<>();
            searchParams.put("activityName", activityName);
            searchParams.put("status", status);
            searchParams.put("startTime", startTime);
            searchParams.put("endTime", endTime);

            Map<String, Object> result = spikeService.getAllActivities(page, pageSize, searchParams);
            return ResultUtil.resultCode(200, "获取成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.resultCode(500, "获取失败：" + e.getMessage());
        }
    }

    /**
     * 添加秒杀商品
     */
    @PostMapping("/admin/goods")
    public Map<String, Object> addSpikeGoods(@RequestBody SpikeGoods spikeGoods) {
        try {
            SpikeGoods result = spikeService.addSpikeGoods(spikeGoods);
            return ResultUtil.resultCode(200, "添加成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.resultCode(500, "添加失败：" + e.getMessage());
        }
    }

    /**
     * 获取活动的商品列表（管理员）
     */
    @GetMapping("/admin/activities/{activityId}/goods")
    public Map<String, Object> getActivityGoods(@PathVariable Long activityId,
                                                 @RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Map<String, Object> result = spikeService.getActivityGoods(activityId, page, pageSize);
            return ResultUtil.resultCode(200, "获取成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.resultCode(500, "获取失败：" + e.getMessage());
        }
    }

    /**
     * 删除秒杀商品
     */
    @DeleteMapping("/admin/goods/{goodsId}")
    public Map<String, Object> deleteSpikeGoods(@PathVariable Long goodsId) {
        try {
            boolean result = spikeService.deleteSpikeGoods(goodsId);
            if (result) {
                return ResultUtil.resultCode(200, "删除成功");
            } else {
                return ResultUtil.resultCode(400, "删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.resultCode(500, "删除失败：" + e.getMessage());
        }
    }

    /**
     * 更新秒杀商品
     */
    @PutMapping("/admin/goods/{goodsId}")
    public Map<String, Object> updateSpikeGoods(@PathVariable Long goodsId,
                                                 @RequestBody SpikeGoods spikeGoods) {
        try {
            spikeGoods.setId(goodsId);
            SpikeGoods result = spikeService.updateSpikeGoods(spikeGoods);
            return ResultUtil.resultCode(200, "更新成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.resultCode(500, "更新失败：" + e.getMessage());
        }
    }

    // 注意：秒杀统计功能已移除，现在使用统一订单系统进行统计分析

    /**
     * 删除秒杀活动
     */
    @DeleteMapping("/admin/activities/{activityId}")
    public Map<String, Object> deleteActivity(@PathVariable Long activityId) {
        try {
            boolean result = spikeService.deleteActivity(activityId);
            if (result) {
                return ResultUtil.resultCode(200, "删除成功");
            } else {
                return ResultUtil.resultCode(400, "删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.resultCode(500, "删除失败：" + e.getMessage());
        }
    }

    /**
     * 更新活动状态
     */
    @PutMapping("/admin/activities/{activityId}/status")
    public Map<String, Object> updateActivityStatus(@PathVariable Long activityId,
                                                     @RequestBody Map<String, Object> params) {
        try {
            Integer status = Integer.valueOf(params.get("status").toString());
            boolean result = spikeService.updateActivityStatus(activityId, status);
            if (result) {
                return ResultUtil.resultCode(200, "更新成功");
            } else {
                return ResultUtil.resultCode(400, "更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.resultCode(500, "更新失败：" + e.getMessage());
        }
    }

    /**
     * 批量操作活动
     */
    @PostMapping("/admin/activities/batch")
    public Map<String, Object> batchOperateActivities(@RequestBody Map<String, Object> params) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> ids = (List<Long>) params.get("ids");
            String operation = params.get("operation").toString();

            if (ids == null || ids.isEmpty()) {
                return ResultUtil.resultCode(400, "请选择要操作的活动");
            }

            Map<String, Object> result = spikeService.batchOperateActivities(ids, operation);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.resultCode(500, "批量操作失败：" + e.getMessage());
        }
    }

    // ========================================
    // 工具方法
    // ========================================

    /**
     * 从Token中获取用户账号
     */
    private String getUserAccountFromToken(HttpServletRequest request) {
        try {
            // 方法1：从SecurityContext中获取（推荐）
            org.springframework.security.core.Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
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

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
