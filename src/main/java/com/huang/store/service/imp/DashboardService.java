package com.huang.store.service.imp;

import java.util.Map;

/**
 * 仪表盘服务接口
 * 
 * @author: 黄龙
 * @date: 2025/07/03
 * @description: 提供管理员仪表盘所需的各种统计数据服务
 */
public interface DashboardService {

    /**
     * 获取仪表盘概览数据
     * 包含：总订单数、总销售额、总用户数、总商品数等核心指标
     */
    Map<String, Object> getDashboardOverview();

    /**
     * 获取销售统计数据
     * @param days 统计天数
     * @return 销售趋势数据
     */
    Map<String, Object> getSalesStatistics(int days);

    /**
     * 获取库存统计数据
     * 包含：总库存、库存紧张商品、缺货商品等
     */
    Map<String, Object> getInventoryStatistics();

    /**
     * 获取客户统计数据
     * 包含：新增用户、活跃用户、用户增长趋势等
     */
    Map<String, Object> getCustomerStatistics();

    /**
     * 获取财务统计数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 财务数据（日结账、销售额、利润等）
     */
    Map<String, Object> getFinancialStatistics(String startDate, String endDate);

    /**
     * 获取热销商品排行
     * @param limit 返回数量限制
     * @return 热销商品列表
     */
    Map<String, Object> getBestsellers(int limit);

    /**
     * 获取最新订单
     * @param limit 返回数量限制
     * @return 最新订单列表
     */
    Map<String, Object> getRecentOrders(int limit);

    /**
     * 获取低库存预警
     * @param threshold 库存阈值
     * @return 低库存商品列表
     */
    Map<String, Object> getLowStockAlerts(int threshold);
}
