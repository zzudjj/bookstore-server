package com.huang.store.service;

import com.huang.store.entity.book.Book;
import com.huang.store.entity.spike.SpikeActivity;
import com.huang.store.entity.spike.SpikeGoods;
import com.huang.store.entity.spike.SpikeRecord;
import com.huang.store.mapper.BookMapper;
import com.huang.store.mapper.SpikeActivityMapper;
import com.huang.store.mapper.SpikeGoodsMapper;
import com.huang.store.mapper.SpikeRecordMapper;
import com.huang.store.mapper.OrderMapper;
import com.huang.store.mapper.ExpenseMapper;
import com.huang.store.entity.order.Order;
import com.huang.store.entity.order.OrderDetail;
import com.huang.store.entity.order.Expense;
import com.huang.store.service.imp.BookService;
import com.huang.store.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.Comparator;

/**
 * 秒杀服务实现类
 */
@Service
public class SpikeService {

    @Autowired
    private SpikeActivityMapper spikeActivityMapper;

    @Autowired
    private SpikeGoodsMapper spikeGoodsMapper;

    @Autowired
    private SpikeRecordMapper spikeRecordMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ExpenseMapper expenseMapper;

    @Autowired
    private BookService bookService;

    // Redis Key前缀
    private static final String SPIKE_STOCK_KEY = "spike:stock:";
    private static final String SPIKE_USER_KEY = "spike:user:";
    private static final String SPIKE_LOCK_KEY = "spike:lock:";

    /**
     * 获取前端秒杀列表（智能筛选最相关的3个活动）
     * 优先显示即将开始的活动，不足时用最近结束的活动补充
     */
    public List<SpikeActivity> getSpikeListForFrontend() {
        List<SpikeActivity> allActivities = spikeActivityMapper.getAllActivitiesOrderByTime();
        LocalDateTime now = LocalDateTime.now();

        // 分类活动
        List<SpikeActivity> upcomingActivities = new ArrayList<>();
        List<SpikeActivity> ongoingActivities = new ArrayList<>();
        List<SpikeActivity> endedActivities = new ArrayList<>();

        for (SpikeActivity activity : allActivities) {
            if (now.isBefore(activity.getStartTime())) {
                upcomingActivities.add(activity);
            } else if (now.isAfter(activity.getStartTime()) && now.isBefore(activity.getEndTime())) {
                ongoingActivities.add(activity);
            } else {
                endedActivities.add(activity);
            }
        }

        // 智能选择活动：优先即将开始的，然后是进行中的，最后是最近结束的
        List<SpikeActivity> selectedActivities = new ArrayList<>();

        // 1. 添加进行中的活动（优先级最高）
        selectedActivities.addAll(ongoingActivities);

        // 2. 添加即将开始的活动（按开始时间排序，最近的优先）
        upcomingActivities.sort(Comparator.comparing(SpikeActivity::getStartTime));
        for (SpikeActivity activity : upcomingActivities) {
            if (selectedActivities.size() >= 3) break;
            selectedActivities.add(activity);
        }

        // 3. 如果还不够3个，用最近结束的活动补充（按结束时间倒序，最近结束的优先）
        if (selectedActivities.size() < 3) {
            endedActivities.sort(Comparator.comparing(SpikeActivity::getEndTime).reversed());
            for (SpikeActivity activity : endedActivities) {
                if (selectedActivities.size() >= 3) break;
                selectedActivities.add(activity);
            }
        }

        // 为选中的活动加载商品列表和计算状态
        for (SpikeActivity activity : selectedActivities) {
            List<SpikeGoods> goodsList = spikeGoodsMapper.getSpikeGoodsByActivity(activity.getId());

            // 为每个秒杀商品设置图书封面
            if (goodsList != null && !goodsList.isEmpty()) {
                for (SpikeGoods goods : goodsList) {
                    // 根据bookId获取图书信息
                    Book book = bookMapper.getBook(goods.getBookId().intValue());
                    if (book != null) {
                        // 获取并设置封面图片
                        String coverImg = bookService.getBookCover(book.getisbn());
                        goods.setCoverImg(coverImg);
                    }
                }
            }

            activity.setSpikeGoodsList(goodsList);

            // 计算进度
            if (goodsList != null && !goodsList.isEmpty()) {
                int totalStock = goodsList.stream().mapToInt(SpikeGoods::getSpikeStock).sum();
                int soldCount = goodsList.stream().mapToInt(g -> g.getSoldCount() != null ? g.getSoldCount() : 0).sum();
                // 这里可以添加进度计算逻辑
            }
        }

        return selectedActivities;
    }

    /**
     * 获取当前进行中的秒杀活动
     */
    public List<SpikeActivity> getActiveActivities() {
        return spikeActivityMapper.getActiveActivities();
    }

    /**
     * 获取指定活动的秒杀商品列表
     */
    public List<SpikeGoods> getSpikeGoodsByActivity(Long activityId) {
        return spikeGoodsMapper.getSpikeGoodsByActivity(activityId);
    }

    /**
     * 获取秒杀商品详情
     */
    public SpikeGoods getSpikeGoodsDetail(Long goodsId) {
        return spikeGoodsMapper.getSpikeGoodsDetail(goodsId);
    }

    /**
     * 根据图书ID执行秒杀
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> spikeBookById(Integer bookId, String userAccount, Integer quantity, String clientIp, String userAgent) {
        // 查找当前进行中的活动中是否有该图书的秒杀商品
        List<SpikeGoods> spikeGoodsList = spikeGoodsMapper.getSpikeGoodsByBookId(bookId);
        
        if (spikeGoodsList == null || spikeGoodsList.isEmpty()) {
            // 该图书暂无秒杀活动，不记录到spikeRecord表
            return ResultUtil.resultCode(404, "该图书暂无秒杀活动");
        }
        
        // 找到第一个进行中的秒杀商品
        SpikeGoods targetGoods = null;
        for (SpikeGoods goods : spikeGoodsList) {
            if (goods.getStatus() == 1 && isActivityValid(goods.getActivityId())) {
                targetGoods = goods;
                break;
            }
        }
        
        if (targetGoods == null) {
            // 该图书的秒杀活动未开始或已结束，不记录到spikeRecord表
            return ResultUtil.resultCode(400, "该图书的秒杀活动未开始或已结束");
        }
        
        // 执行秒杀
        return doSpike(targetGoods.getId(), userAccount, quantity, clientIp, userAgent);
    }

    /**
     * 执行秒杀 - 核心业务逻辑
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> doSpike(Long spikeGoodsId, String userAccount, Integer quantity, String clientIp, String userAgent) {
        // 1. 参数校验
        if (spikeGoodsId == null || StringUtils.isEmpty(userAccount) || quantity <= 0) {
            recordSpikeResult(spikeGoodsId, userAccount, quantity, false, "参数错误", clientIp, userAgent);
            return ResultUtil.resultCode(400, "参数错误");
        }

        // 2. 获取秒杀商品信息
        SpikeGoods spikeGoods = spikeGoodsMapper.getSpikeGoodsDetail(spikeGoodsId);
        if (spikeGoods == null) {
            recordSpikeResult(spikeGoodsId, userAccount, quantity, false, "商品不存在", clientIp, userAgent);
            return ResultUtil.resultCode(404, "秒杀商品不存在");
        }

        // 3. 检查商品状态
        if (spikeGoods.getStatus() != 1) {
            recordSpikeResult(spikeGoodsId, userAccount, quantity, false, "商品已下架", clientIp, userAgent);
            return ResultUtil.resultCode(400, "商品已下架");
        }

        // 4. 检查活动状态
        if (!isActivityValid(spikeGoods.getActivityId())) {
            recordSpikeResult(spikeGoodsId, userAccount, quantity, false, "活动未开始或已结束", clientIp, userAgent);
            return ResultUtil.resultCode(400, "活动未开始或已结束");
        }

        // 5. 检查用户购买限制
        if (!checkUserLimit(spikeGoodsId, userAccount, quantity, spikeGoods.getLimitPerUser())) {
            recordSpikeResult(spikeGoodsId, userAccount, quantity, false, "超出购买限制", clientIp, userAgent);
            return ResultUtil.resultCode(400, "超出购买限制，每人限购" + spikeGoods.getLimitPerUser() + "件");
        }

        // 6. 分布式锁控制并发
        String lockKey = SPIKE_LOCK_KEY + spikeGoodsId;
        String lockValue = UUID.randomUUID().toString();

        try {
            // 获取分布式锁，超时时间10秒
            Boolean lockResult = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 10, TimeUnit.SECONDS);
            if (!lockResult) {
                recordSpikeResult(spikeGoodsId, userAccount, quantity, false, "系统繁忙", clientIp, userAgent);
                return ResultUtil.resultCode(429, "系统繁忙，请稍后重试");
            }

            // 7. 再次检查库存（双重检查）
            SpikeGoods currentGoods = spikeGoodsMapper.getSpikeGoodsDetail(spikeGoodsId);
            if (currentGoods.getRemainStock() < quantity) {
                recordSpikeResult(spikeGoodsId, userAccount, quantity, false, "库存不足", clientIp, userAgent);
                return ResultUtil.resultCode(400, "库存不足");
            }

            // 8. 扣减库存（同时扣减秒杀库存和图书库存）
            int spikeStockResult = spikeGoodsMapper.reduceStock(spikeGoodsId, quantity);
            if (spikeStockResult <= 0) {
                recordSpikeResult(spikeGoodsId, userAccount, quantity, false, "秒杀库存扣减失败", clientIp, userAgent);
                return ResultUtil.resultCode(400, "库存不足");
            }

            // 同时扣减图书表的库存
            int bookStockResult = bookMapper.modifyBookStock(spikeGoods.getBookId(), quantity);
            if (bookStockResult <= 0) {
                // 如果图书库存扣减失败，需要回滚秒杀库存
                spikeGoodsMapper.restoreStock(spikeGoodsId, quantity);
                recordSpikeResult(spikeGoodsId, userAccount, quantity, false, "图书库存不足", clientIp, userAgent);
                return ResultUtil.resultCode(400, "图书库存不足");
            }

            // 9. 创建统一订单（使用现有订单系统）
            String orderId = createUnifiedOrder(spikeGoods, userAccount, quantity);

            // 10. 记录用户购买数量
            recordUserPurchase(spikeGoodsId, userAccount, quantity);

            // 11. 记录成功结果
            recordSpikeResult(spikeGoods.getId(), userAccount, quantity, true, "秒杀成功", clientIp, userAgent);

            // 12. 返回订单信息
            Map<String, Object> orderInfo = new HashMap<>();
            orderInfo.put("orderId", orderId);
            orderInfo.put("spikePrice", spikeGoods.getSpikePrice());
            orderInfo.put("totalAmount", spikeGoods.getSpikePrice().multiply(new BigDecimal(quantity)));
            orderInfo.put("quantity", quantity);

            return ResultUtil.resultCode(200, "秒杀成功", orderInfo);

        } finally {
            // 释放分布式锁
            releaseLock(lockKey, lockValue);
        }
    }

    /**
     * 检查活动是否有效
     */
    private boolean isActivityValid(Long activityId) {
        SpikeActivity activity = spikeActivityMapper.getActivityById(activityId);
        if (activity == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        return activity.getStatus() == 1 && 
               now.isAfter(activity.getStartTime()) && 
               now.isBefore(activity.getEndTime());
    }

    /**
     * 检查用户购买限制
     */
    private boolean checkUserLimit(Long spikeGoodsId, String userAccount, Integer quantity, Integer limitPerUser) {
        // 检查数据库中的实际成功购买记录
        int purchasedCount = spikeRecordMapper.getUserSuccessPurchaseCount(userAccount, spikeGoodsId);

        // 检查Redis缓存
        String userKey = SPIKE_USER_KEY + spikeGoodsId + ":" + userAccount;
        Integer cachedCount = (Integer) redisTemplate.opsForValue().get(userKey);

        // 使用较大的值作为已购买数量
        int actualPurchased = Math.max(purchasedCount, cachedCount != null ? cachedCount : 0);

        return (actualPurchased + quantity) <= limitPerUser;
    }

    /**
     * 记录用户购买数量
     */
    private void recordUserPurchase(Long spikeGoodsId, String userAccount, Integer quantity) {
        String userKey = SPIKE_USER_KEY + spikeGoodsId + ":" + userAccount;
        Integer purchased = (Integer) redisTemplate.opsForValue().get(userKey);
        if (purchased == null) {
            purchased = 0;
        }
        redisTemplate.opsForValue().set(userKey, purchased + quantity, 24, TimeUnit.HOURS);
    }

    /**
     * 创建统一订单（使用现有订单系统）
     */
    private String createUnifiedOrder(SpikeGoods spikeGoods, String userAccount, Integer quantity) {
        // 1. 生成订单号
        String orderId = "SPIKE" + System.currentTimeMillis() + (int)(Math.random() * 1000);

        // 2. 创建订单主表记录
        Order order = new Order();
        order.setOrderId(orderId);
        order.setAccount(userAccount);
        order.setAddressId(1); // 使用默认地址，后续可让用户补充
        order.setOrderTime(new java.sql.Timestamp(System.currentTimeMillis()));
        order.setOrderStatus("待付款");
        order.setBeUserDelete(false);
        orderMapper.addOrder(order);

        // 3. 创建订单明细记录
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderId);
        orderDetail.setBookId(spikeGoods.getBookId());
        orderDetail.setNum(quantity);
        orderDetail.setPrice(spikeGoods.getSpikePrice().doubleValue());

        List<OrderDetail> orderDetailList = new ArrayList<>();
        orderDetailList.add(orderDetail);
        orderMapper.batchAddOrderDetail(orderDetailList);

        // 4. 创建费用记录
        BigDecimal spikePrice = spikeGoods.getSpikePrice();
        BigDecimal originalPrice = spikeGoods.getOriginalPrice();
        BigDecimal totalAmount = spikePrice.multiply(new BigDecimal(quantity));
        BigDecimal originalTotal = originalPrice.multiply(new BigDecimal(quantity));
        BigDecimal discount = originalTotal.subtract(totalAmount);

        Expense expense = new Expense();
        expense.setOrderId(orderId);
        expense.setProductTotalMoney(originalTotal.doubleValue()); // 商品总价（原价）
        expense.setFreight(0.0);
        expense.setCoupon(0);
        expense.setActivityDiscount(discount.doubleValue()); // 秒杀折扣
        expense.setAllPrice(originalTotal.doubleValue()); // 订单总金额（原价）
        expense.setFinallyPrice(totalAmount.doubleValue()); // 最终实付金额（秒杀价）
        expenseMapper.addExpense(expense);

        return orderId;
    }

    /**
     * 记录秒杀结果
     */
    private void recordSpikeResult(Long spikeGoodsId, String userAccount, Integer quantity, boolean success, String failReason, String clientIp, String userAgent) {
        try {
            SpikeRecord record = new SpikeRecord();
            record.setSpikeGoodsId(spikeGoodsId);
            record.setUserAccount(userAccount);
            record.setQuantity(quantity);
            record.setSpikeTime(LocalDateTime.now());
            record.setResult(success ? 1 : 0);
            record.setFailReason(success ? null : failReason);
            record.setIpAddress(clientIp);
            record.setUserAgent(userAgent);
            spikeRecordMapper.insertSpikeRecord(record);
        } catch (Exception e) {
            // 记录失败不影响主流程
            e.printStackTrace();
        }
    }

    /**
     * 释放分布式锁
     */
    private void releaseLock(String lockKey, String lockValue) {
        try {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            redisTemplate.execute(
                (org.springframework.data.redis.core.script.RedisScript<Long>) 
                org.springframework.data.redis.core.script.RedisScript.of(script, Long.class),
                Collections.singletonList(lockKey),
                lockValue
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 注意：获取用户秒杀订单列表功能已移除，现在使用统一订单系统查询用户订单

    // 注意：获取秒杀订单详情功能已移除，现在使用统一订单系统查询订单详情

    // 注意：取消秒杀订单功能已移除，现在使用统一订单系统进行订单管理

    // ========================================
    // 管理员功能
    // ========================================

    /**
     * 创建秒杀活动
     */
    public SpikeActivity createActivity(SpikeActivity activity) {
        activity.setCreateTime(LocalDateTime.now());
        activity.setUpdateTime(LocalDateTime.now());
        spikeActivityMapper.insertActivity(activity);
        return activity;
    }

    /**
     * 根据ID获取活动
     */
    public SpikeActivity getActivityById(Long activityId) {
        return spikeActivityMapper.getActivityById(activityId);
    }

    /**
     * 更新秒杀活动
     */
    @Transactional(rollbackFor = Exception.class)
    public SpikeActivity updateActivity(SpikeActivity activity) {
        try {
            activity.setUpdateTime(LocalDateTime.now());
            int result = spikeActivityMapper.updateActivity(activity);
            if (result > 0) {
                return spikeActivityMapper.getActivityById(activity.getId());
            } else {
                throw new RuntimeException("更新活动失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("更新活动失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有秒杀活动
     */
    public Map<String, Object> getAllActivities(Integer page, Integer pageSize) {
        return getAllActivities(page, pageSize, null);
    }

    /**
     * 获取所有秒杀活动（带搜索条件）
     */
    public Map<String, Object> getAllActivities(Integer page, Integer pageSize, Map<String, Object> searchParams) {
        int offset = (page - 1) * pageSize;
        List<SpikeActivity> activities;
        int total;

        if (searchParams != null && !searchParams.isEmpty()) {
            // 有搜索条件
            activities = spikeActivityMapper.searchActivities(offset, pageSize, searchParams);
            total = spikeActivityMapper.getSearchActivityCount(searchParams);
        } else {
            // 无搜索条件
            activities = spikeActivityMapper.getAllActivities(offset, pageSize);
            total = spikeActivityMapper.getActivityCount();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("activities", activities);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }

    /**
     * 添加秒杀商品
     */
    public SpikeGoods addSpikeGoods(SpikeGoods spikeGoods) {
        // 验证图书库存
        Book book = bookMapper.getBook(spikeGoods.getBookId());
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }

        if (spikeGoods.getSpikeStock() > book.getStock()) {
            throw new RuntimeException("秒杀库存不能超过图书库存，当前图书库存：" + book.getStock());
        }

        // 设置原价
        spikeGoods.setOriginalPrice(BigDecimal.valueOf(book.getPrice()));
        spikeGoods.setCreateTime(LocalDateTime.now());
        spikeGoods.setUpdateTime(LocalDateTime.now());
        spikeGoodsMapper.insertSpikeGoods(spikeGoods);
        return spikeGoods;
    }

    /**
     * 获取活动的商品列表（分页）
     */
    public Map<String, Object> getActivityGoods(Long activityId, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<SpikeGoods> goodsList = spikeGoodsMapper.getActivityGoodsPaged(activityId, offset, pageSize);
        int total = spikeGoodsMapper.getActivityGoodsCount(activityId);

        Map<String, Object> result = new HashMap<>();
        result.put("goods", goodsList);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);

        return result;
    }

    /**
     * 删除秒杀商品
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSpikeGoods(Long goodsId) {
        try {
            int result = spikeGoodsMapper.deleteSpikeGoods(goodsId);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("删除秒杀商品失败：" + e.getMessage());
        }
    }

    /**
     * 更新秒杀商品
     */
    @Transactional(rollbackFor = Exception.class)
    public SpikeGoods updateSpikeGoods(SpikeGoods spikeGoods) {
        try {
            spikeGoods.setUpdateTime(LocalDateTime.now());
            int result = spikeGoodsMapper.updateSpikeGoods(spikeGoods);
            if (result > 0) {
                return spikeGoodsMapper.getSpikeGoodsById(spikeGoods.getId());
            } else {
                throw new RuntimeException("更新秒杀商品失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("更新秒杀商品失败：" + e.getMessage());
        }
    }

    // 注意：秒杀统计功能已移除，现在使用统一订单系统进行统计分析

    /**
     * 删除秒杀活动
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteActivity(Long activityId) {
        try {
            // 1. 检查活动是否存在
            SpikeActivity activity = spikeActivityMapper.getActivityById(activityId);
            if (activity == null) {
                return false;
            }

            // 2. 检查活动是否可以删除（只能删除未开始或已结束的活动）
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(activity.getStartTime()) && now.isBefore(activity.getEndTime())) {
                throw new RuntimeException("正在进行中的活动不能删除");
            }

            // 3. 删除相关的秒杀商品
            spikeGoodsMapper.deleteByActivityId(activityId);

            // 4. 删除活动
            int result = spikeActivityMapper.deleteActivity(activityId);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("删除活动失败：" + e.getMessage());
        }
    }

    /**
     * 更新活动状态
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateActivityStatus(Long activityId, Integer status) {
        try {
            int result = spikeActivityMapper.updateActivityStatus(activityId, status);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("更新活动状态失败：" + e.getMessage());
        }
    }

    /**
     * 批量操作活动
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> batchOperateActivities(List<Long> ids, String operation) {
        try {
            int successCount = 0;
            int failCount = 0;

            for (Long id : ids) {
                try {
                    boolean result = false;
                    switch (operation) {
                        case "delete":
                            result = deleteActivity(id);
                            break;
                        case "enable":
                            result = updateActivityStatus(id, 1);
                            break;
                        case "disable":
                            result = updateActivityStatus(id, 0);
                            break;
                        default:
                            throw new RuntimeException("不支持的操作类型：" + operation);
                    }

                    if (result) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch (Exception e) {
                    failCount++;
                    System.err.println("批量操作失败，活动ID：" + id + "，错误：" + e.getMessage());
                }
            }

            String message = String.format("操作完成：成功 %d 个，失败 %d 个", successCount, failCount);
            return ResultUtil.resultCode(200, message);

        } catch (Exception e) {
            throw new RuntimeException("批量操作失败：" + e.getMessage());
        }
    }
}
