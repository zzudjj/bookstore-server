package com.huang.store.service;

import com.huang.store.entity.order.Order;
import com.huang.store.service.imp.OrderService;
import com.huang.store.service.imp.OrderConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单超时处理服务
 * 
 * @author Augment Agent
 * @date 2025-07-02
 * @description 处理订单超时自动取消等定时任务
 */
@Service
public class OrderTimeoutService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderTimeoutService.class);
    
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderConfigService orderConfigService;
    
    /**
     * 每5分钟检查一次超时订单
     * 自动取消超过配置时间未支付的订单
     */
    @Scheduled(fixedRate = 300000) // 5分钟 = 300000毫秒
    public void cancelTimeoutOrders() {
        logger.debug("开始检查超时订单...");

        try {
            // 从配置中获取正常订单超时时间
            Integer timeoutMinutes = orderConfigService.getNormalPaymentTimeout();
            logger.debug("使用配置的超时时间: {}分钟", timeoutMinutes);

            // 查找超时的待支付订单
            List<Order> timeoutOrders = orderService.findTimeoutPendingOrders(timeoutMinutes);
            
            if (timeoutOrders.isEmpty()) {
                logger.debug("没有发现超时订单");
                return;
            }
            
            logger.info("发现 {} 个超时订单，开始处理...", timeoutOrders.size());
            
            int successCount = 0;
            int failCount = 0;
            
            for (Order order : timeoutOrders) {
                try {
                    // 释放预留库存
                    orderService.releaseReservedStock(order.getOrderId());

                    // 更新订单状态为已取消
                    orderService.modifyOrderStatus(order.getId(), "已取消");

                    logger.info("成功取消超时订单: 订单ID={}, 用户={}",
                        order.getOrderId(), order.getAccount());
                    successCount++;

                } catch (Exception e) {
                    logger.error("取消超时订单失败: 订单ID={}, 错误={}",
                        order.getOrderId(), e.getMessage(), e);
                    failCount++;
                }
            }
            
            logger.info("超时订单处理完成: 成功 {} 个, 失败 {} 个", successCount, failCount);
            
        } catch (Exception e) {
            logger.error("检查超时订单时发生异常", e);
        }
    }
    

}
