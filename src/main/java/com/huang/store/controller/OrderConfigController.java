package com.huang.store.controller;

import com.huang.store.entity.order.OrderConfig;
import com.huang.store.service.imp.OrderConfigService;
import com.huang.store.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 订单配置Controller
 * 
 * @author Augment Agent
 * @date 2025-07-03
 * @description 订单配置管理接口
 */
@RestController
@RequestMapping("/order/config")
public class OrderConfigController {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderConfigController.class);
    
    @Autowired
    private OrderConfigService orderConfigService;
    
    /**
     * 获取所有订单配置
     */
    @GetMapping("/list")
    public Map<String, Object> getAllConfigs() {
        try {
            List<OrderConfig> configs = orderConfigService.getAllConfigs();
            logger.info("获取订单配置列表成功，共{}个配置", configs.size());
            return ResultUtil.resultCode(200, "获取成功", configs);
        } catch (Exception e) {
            logger.error("获取订单配置列表失败", e);
            return ResultUtil.resultCode(500, "获取失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取配置映射（键值对形式）
     */
    @GetMapping("/map")
    public Map<String, Object> getConfigMap() {
        try {
            Map<String, String> configMap = orderConfigService.getConfigMap();
            logger.info("获取订单配置映射成功，共{}个配置", configMap.size());
            return ResultUtil.resultCode(200, "获取成功", configMap);
        } catch (Exception e) {
            logger.error("获取订单配置映射失败", e);
            return ResultUtil.resultCode(500, "获取失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据配置键获取配置值
     */
    @GetMapping("/{configKey}")
    public Map<String, Object> getConfigValue(@PathVariable String configKey) {
        try {
            String value = orderConfigService.getConfigValue(configKey);
            if (value != null) {
                logger.info("获取配置成功: {} = {}", configKey, value);
                return ResultUtil.resultCode(200, "获取成功", value);
            } else {
                logger.warn("配置不存在: {}", configKey);
                return ResultUtil.resultCode(404, "配置不存在");
            }
        } catch (Exception e) {
            logger.error("获取配置失败: {}", configKey, e);
            return ResultUtil.resultCode(500, "获取失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新单个配置
     */
    @PutMapping("/{configKey}")
    public Map<String, Object> updateConfig(@PathVariable String configKey, 
                                          @RequestParam String configValue) {
        try {
            boolean success = orderConfigService.updateConfig(configKey, configValue);
            if (success) {
                logger.info("更新配置成功: {} = {}", configKey, configValue);
                return ResultUtil.resultCode(200, "更新成功");
            } else {
                logger.warn("更新配置失败: {} = {}", configKey, configValue);
                return ResultUtil.resultCode(500, "更新失败");
            }
        } catch (Exception e) {
            logger.error("更新配置异常: {} = {}", configKey, configValue, e);
            return ResultUtil.resultCode(500, "更新失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量更新配置
     */
    @PutMapping("/batch")
    public Map<String, Object> batchUpdateConfigs(@RequestBody Map<String, String> configMap) {
        try {
            boolean success = orderConfigService.batchUpdateConfigs(configMap);
            if (success) {
                logger.info("批量更新配置成功，共{}个配置", configMap.size());
                return ResultUtil.resultCode(200, "批量更新成功");
            } else {
                logger.warn("批量更新配置部分失败，共{}个配置", configMap.size());
                return ResultUtil.resultCode(500, "批量更新部分失败");
            }
        } catch (Exception e) {
            logger.error("批量更新配置异常", e);
            return ResultUtil.resultCode(500, "批量更新失败：" + e.getMessage());
        }
    }
}
