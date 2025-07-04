package com.huang.store.service;

import com.huang.store.entity.book.Book;
import com.huang.store.mapper.*;
import com.huang.store.service.imp.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Calendar;

/**
 * 仪表盘服务实现类
 * 
 * @author: 黄龙
 * @date: 2025/07/03
 * @description: 实现管理员仪表盘所需的各种统计数据服务
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BookMapper bookMapper;



    @Override
    public Map<String, Object> getDashboardOverview() {
        Map<String, Object> overview = new HashMap<>();

        try {
            // 总订单数
            int totalOrders = orderMapper.count(null, null, false);
            overview.put("totalOrders", totalOrders);

            // 总用户数
            int totalUsers = userMapper.count();
            overview.put("totalUsers", totalUsers);

            // 总商品数
            int totalBooks = bookMapper.getBookCount();
            overview.put("totalBooks", totalBooks);

            // 今日订单数和销售额
            LocalDate today = LocalDate.now();
            String todayStr = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            int todayOrders = getTodayOrderCount(todayStr);
            BigDecimal todaySales = getTodaySales(todayStr);
            overview.put("todayOrders", todayOrders);
            overview.put("todaySales", todaySales);

            // 昨日销售额（用于计算增长率）
            LocalDate yesterday = today.minusDays(1);
            String yesterdayStr = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            BigDecimal yesterdaySales = getTodaySales(yesterdayStr);
            overview.put("yesterdaySales", yesterdaySales);

            // 计算销售增长率
            double salesGrowthRate = 0.0;
            if (yesterdaySales.compareTo(BigDecimal.ZERO) > 0) {
                salesGrowthRate = todaySales.subtract(yesterdaySales)
                    .divide(yesterdaySales, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal(100)).doubleValue();
            }
            overview.put("salesGrowthRate", salesGrowthRate);

            // 本月新增用户（暂时返回0，需要扩展UserMapper）
            overview.put("monthlyNewUsers", 0);

            // 待处理订单数
            int pendingOrders = orderMapper.count(null, "待发货", false);
            overview.put("pendingOrders", pendingOrders);

            System.out.println("仪表盘概览数据: " + overview);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("获取仪表盘概览数据失败: " + e.getMessage());
            // 如果出错，返回默认值
            overview.put("totalOrders", 0);
            overview.put("totalUsers", 0);
            overview.put("totalBooks", 0);
            overview.put("todayOrders", 0);
            overview.put("todaySales", BigDecimal.ZERO);
            overview.put("yesterdaySales", BigDecimal.ZERO);
            overview.put("salesGrowthRate", 0.0);
            overview.put("monthlyNewUsers", 0);
            overview.put("pendingOrders", 0);
        }

        return overview;
    }

    @Override
    public Map<String, Object> getSalesStatistics(int days) {
        Map<String, Object> sales = new HashMap<>();
        
        try {
            // 获取最近N天的销售数据
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days - 1);
            
            String startDateStr = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String endDateStr = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            // 调用现有的订单统计方法
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date start = format.parse(startDateStr);
            Date end = format.parse(endDateStr);
            
            var orderStatistics = orderMapper.getOrderStatistic(
                new Timestamp(start.getTime()), 
                new Timestamp(end.getTime())
            );
            
            // 构造完整的日期序列
            List<String> dateLabels = new ArrayList<>();
            List<Integer> orderCounts = new ArrayList<>();
            List<BigDecimal> salesAmounts = new ArrayList<>();
            
            for (int i = 0; i < days; i++) {
                LocalDate currentDate = startDate.plusDays(i);
                String dateStr = currentDate.format(DateTimeFormatter.ofPattern("MM-dd"));
                dateLabels.add(dateStr);
                
                // 查找对应日期的数据
                boolean found = false;
                for (var stat : orderStatistics) {
                    // 将Timestamp转换为日期字符串进行比较
                    String statDateStr = new SimpleDateFormat("yyyy-MM-dd").format(stat.getOrderTime());
                    if (statDateStr.equals(currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))) {
                        orderCounts.add(stat.getCount());
                        salesAmounts.add(BigDecimal.valueOf(stat.getAmount()));
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    orderCounts.add(0);
                    salesAmounts.add(BigDecimal.ZERO);
                }
            }
            
            sales.put("dateLabels", dateLabels);
            sales.put("orderCounts", orderCounts);
            sales.put("salesAmounts", salesAmounts);
            
        } catch (Exception e) {
            e.printStackTrace();
            sales.put("dateLabels", new ArrayList<>());
            sales.put("orderCounts", new ArrayList<>());
            sales.put("salesAmounts", new ArrayList<>());
        }
        
        return sales;
    }

    @Override
    public Map<String, Object> getInventoryStatistics() {
        Map<String, Object> inventory = new HashMap<>();

        try {
            // 总库存
            int totalStock = bookMapper.getTotalStock();
            inventory.put("totalStock", totalStock);

            // 缺货商品（库存 = 0）
            int outOfStockCount = bookMapper.getLowStockCount(0);
            inventory.put("outOfStockCount", outOfStockCount);

            // 库存紧张商品（0 < 库存 <= 20）
            int lowStockCount = bookMapper.getLowStockCount(20) - outOfStockCount;
            inventory.put("lowStockCount", lowStockCount);

            // 正常库存商品（库存 > 20）
            int normalStockCount = bookMapper.getBookCount() - bookMapper.getLowStockCount(20);
            inventory.put("normalStockCount", normalStockCount);

            // 添加阈值信息
            inventory.put("thresholds", Map.of(
                "outOfStock", 0,
                "lowStock", 20,
                "normalStock", ">20"
            ));

            System.out.println("库存统计数据: " + inventory);

        } catch (Exception e) {
            e.printStackTrace();
            inventory.put("totalStock", 0);
            inventory.put("lowStockCount", 0);
            inventory.put("outOfStockCount", 0);
            inventory.put("normalStockCount", 0);
        }

        return inventory;
    }

    @Override
    public Map<String, Object> getCustomerStatistics() {
        Map<String, Object> customers = new HashMap<>();
        
        try {
            // 总用户数
            int totalUsers = userMapper.count();
            customers.put("totalUsers", totalUsers);

            // 今日新增用户
            int todayNewUsers = getTodayNewUsers();
            customers.put("todayNewUsers", todayNewUsers);

            // 本月新增用户
            int monthlyNewUsers = getMonthlyNewUsers();
            customers.put("monthlyNewUsers", monthlyNewUsers);

            // 活跃用户（最近30天有订单的用户）
            int activeUsers = getActiveUsers(30);
            customers.put("activeUsers", activeUsers);

        } catch (Exception e) {
            e.printStackTrace();
            customers.put("totalUsers", 0);
            customers.put("todayNewUsers", 0);
            customers.put("monthlyNewUsers", 0);
            customers.put("activeUsers", 0);
        }

        return customers;
    }

    @Override
    public Map<String, Object> getFinancialStatistics(String startDate, String endDate) {
        Map<String, Object> financial = new HashMap<>();
        
        try {
            // 如果没有指定日期，默认查询今天
            if (startDate == null || endDate == null) {
                LocalDate today = LocalDate.now();
                startDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                endDate = startDate;
            }

            // 日销售额
            BigDecimal dailySales = getDailySales(startDate, endDate);
            financial.put("dailySales", dailySales);

            // 日订单数
            int dailyOrders = getDailyOrderCount(startDate, endDate);
            financial.put("dailyOrders", dailyOrders);

            // 平均订单金额
            BigDecimal avgOrderAmount = BigDecimal.ZERO;
            if (dailyOrders > 0) {
                avgOrderAmount = dailySales.divide(new BigDecimal(dailyOrders), 2, BigDecimal.ROUND_HALF_UP);
            }
            financial.put("avgOrderAmount", avgOrderAmount);

            // 简单的利润估算（假设利润率为20%）
            BigDecimal estimatedProfit = dailySales.multiply(new BigDecimal("0.20"));
            financial.put("estimatedProfit", estimatedProfit);

        } catch (Exception e) {
            e.printStackTrace();
            financial.put("dailySales", BigDecimal.ZERO);
            financial.put("dailyOrders", 0);
            financial.put("avgOrderAmount", BigDecimal.ZERO);
            financial.put("estimatedProfit", BigDecimal.ZERO);
        }

        return financial;
    }

    @Override
    public Map<String, Object> getBestsellers(int limit) {
        Map<String, Object> bestsellers = new HashMap<>();
        
        try {
            // 获取热销商品（这里需要在BookMapper中添加相应方法）
            // 暂时返回空列表，后续可以扩展
            bestsellers.put("books", new ArrayList<>());
            bestsellers.put("total", 0);
            
        } catch (Exception e) {
            e.printStackTrace();
            bestsellers.put("books", new ArrayList<>());
            bestsellers.put("total", 0);
        }

        return bestsellers;
    }

    @Override
    public Map<String, Object> getRecentOrders(int limit) {
        Map<String, Object> orders = new HashMap<>();
        
        try {
            // 获取最新订单（使用现有的orderDtoList方法）
            var recentOrders = orderMapper.orderDtoList(null, 0, limit, null, false);
            orders.put("orders", recentOrders);
            orders.put("total", recentOrders.size());
            
        } catch (Exception e) {
            e.printStackTrace();
            orders.put("orders", new ArrayList<>());
            orders.put("total", 0);
        }

        return orders;
    }

    @Override
    public Map<String, Object> getLowStockAlerts(int threshold) {
        Map<String, Object> alerts = new HashMap<>();

        try {
            // 获取低库存商品列表
            List<Book> lowStockBooks = bookMapper.getLowStockBooks(threshold, 0, 10);
            alerts.put("books", lowStockBooks);
            alerts.put("total", lowStockBooks.size());

        } catch (Exception e) {
            e.printStackTrace();
            alerts.put("books", new ArrayList<>());
            alerts.put("total", 0);
        }

        return alerts;
    }

    // 辅助方法
    private int getTodayOrderCount(String date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = format.parse(date);

            // 结束时间设置为当天的23:59:59
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            Date endDate = cal.getTime();

            var statistics = orderMapper.getOrderStatistic(
                new Timestamp(startDate.getTime()),
                new Timestamp(endDate.getTime())
            );

            return statistics.isEmpty() ? 0 : statistics.get(0).getCount();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private BigDecimal getTodaySales(String date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = format.parse(date);

            // 结束时间设置为当天的23:59:59
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            Date endDate = cal.getTime();

            var statistics = orderMapper.getOrderStatistic(
                new Timestamp(startDate.getTime()),
                new Timestamp(endDate.getTime())
            );

            return statistics.isEmpty() ? BigDecimal.ZERO : BigDecimal.valueOf(statistics.get(0).getAmount());
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    private int getTodayNewUsers() {
        // 这里需要在UserMapper中添加按日期查询新用户的方法
        // 暂时返回0，后续可以扩展
        return 0;
    }

    private int getMonthlyNewUsers() {
        // 这里需要在UserMapper中添加按月查询新用户的方法
        // 暂时返回0，后续可以扩展
        return 0;
    }

    private int getActiveUsers(int days) {
        // 这里需要查询最近N天有订单的用户数
        // 暂时返回0，后续可以扩展
        return 0;
    }

    private BigDecimal getDailySales(String startDate, String endDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date start = format.parse(startDate);
            Date end = format.parse(endDate);

            // 结束时间设置为当天的23:59:59
            Calendar cal = Calendar.getInstance();
            cal.setTime(end);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            Date endDateTime = cal.getTime();

            var statistics = orderMapper.getOrderStatistic(
                new Timestamp(start.getTime()),
                new Timestamp(endDateTime.getTime())
            );

            return statistics.stream()
                .map(stat -> BigDecimal.valueOf(stat.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    private int getDailyOrderCount(String startDate, String endDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date start = format.parse(startDate);
            Date end = format.parse(endDate);

            // 结束时间设置为当天的23:59:59
            Calendar cal = Calendar.getInstance();
            cal.setTime(end);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            Date endDateTime = cal.getTime();

            var statistics = orderMapper.getOrderStatistic(
                new Timestamp(start.getTime()),
                new Timestamp(endDateTime.getTime())
            );

            return statistics.stream()
                .mapToInt(stat -> stat.getCount())
                .sum();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
