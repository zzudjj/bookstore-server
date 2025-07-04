package com.huang.store.controller;

import com.huang.store.service.imp.DashboardService;
import com.huang.store.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员仪表盘控制器
 * 
 * @author: 黄龙
 * @date: 2025/07/03
 * @description: 提供管理员仪表盘所需的各种统计数据
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * 获取仪表盘概览数据
     */
    @GetMapping("/overview")
    public Map<String, Object> getDashboardOverview() {
        try {
            Map<String, Object> overview = dashboardService.getDashboardOverview();
            return ResultUtil.resultSuccess(overview);
        } catch (Exception e) {
            return ResultUtil.resultCode(500, "获取仪表盘数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取销售统计数据
     */
    @GetMapping("/sales")
    public Map<String, Object> getSalesStatistics(
            @RequestParam(value = "period", defaultValue = "7") int days) {
        try {
            Map<String, Object> sales = dashboardService.getSalesStatistics(days);
            return ResultUtil.resultSuccess(sales);
        } catch (Exception e) {
            return ResultUtil.resultCode(500, "获取销售统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取库存统计数据
     */
    @GetMapping("/inventory")
    public Map<String, Object> getInventoryStatistics() {
        try {
            Map<String, Object> inventory = dashboardService.getInventoryStatistics();
            return ResultUtil.resultSuccess(inventory);
        } catch (Exception e) {
            return ResultUtil.resultCode(500, "获取库存统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取客户统计数据
     */
    @GetMapping("/customers")
    public Map<String, Object> getCustomerStatistics() {
        try {
            Map<String, Object> customers = dashboardService.getCustomerStatistics();
            return ResultUtil.resultSuccess(customers);
        } catch (Exception e) {
            return ResultUtil.resultCode(500, "获取客户统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取财务统计数据
     */
    @GetMapping("/financial")
    public Map<String, Object> getFinancialStatistics(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        try {
            Map<String, Object> financial = dashboardService.getFinancialStatistics(startDate, endDate);
            return ResultUtil.resultSuccess(financial);
        } catch (Exception e) {
            return ResultUtil.resultCode(500, "获取财务统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取热销商品排行
     */
    @GetMapping("/bestsellers")
    public Map<String, Object> getBestsellers(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        try {
            Map<String, Object> bestsellers = dashboardService.getBestsellers(limit);
            return ResultUtil.resultSuccess(bestsellers);
        } catch (Exception e) {
            return ResultUtil.resultCode(500, "获取热销商品失败: " + e.getMessage());
        }
    }

    /**
     * 获取最新订单
     */
    @GetMapping("/recent-orders")
    public Map<String, Object> getRecentOrders(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        try {
            Map<String, Object> orders = dashboardService.getRecentOrders(limit);
            return ResultUtil.resultSuccess(orders);
        } catch (Exception e) {
            return ResultUtil.resultCode(500, "获取最新订单失败: " + e.getMessage());
        }
    }

    /**
     * 获取低库存预警
     */
    @GetMapping("/low-stock")
    public Map<String, Object> getLowStockAlerts(
            @RequestParam(value = "threshold", defaultValue = "10") int threshold) {
        try {
            Map<String, Object> alerts = dashboardService.getLowStockAlerts(threshold);
            return ResultUtil.resultSuccess(alerts);
        } catch (Exception e) {
            return ResultUtil.resultCode(500, "获取库存预警失败: " + e.getMessage());
        }
    }
}
