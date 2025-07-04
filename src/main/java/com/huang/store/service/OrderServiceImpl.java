package com.huang.store.service;

import com.huang.store.entity.book.Book;
import com.huang.store.entity.dto.*;
import com.huang.store.entity.order.Expense;
import com.huang.store.entity.order.Order;
import com.huang.store.entity.order.OrderDetail;
import com.huang.store.entity.order.StockReservation;
import com.huang.store.entity.dto.CouponCalculationResult;
import com.huang.store.entity.dto.CouponUsageRequest;
import com.huang.store.enums.OrderStatusEnum;
import com.huang.store.entity.user.User;
import com.huang.store.entity.user.Address;
import com.huang.store.mapper.BookMapper;
import com.huang.store.mapper.ExpenseMapper;
import com.huang.store.mapper.OrderMapper;
import com.huang.store.mapper.StockReservationMapper;
import com.huang.store.service.imp.OrderService;
import com.huang.store.service.imp.AddressService;
import com.huang.store.service.imp.CouponService;
import com.huang.store.service.imp.BookService;
import com.huang.store.util.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author: 黄龙
 * @date: 2020/7/22 8:59
 * @description:
 */
@Service("orderService")
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    ExpenseMapper expenseMapper;

    @Autowired
    BookMapper bookMapper;

    @Autowired
    StockReservationMapper stockReservationMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    AddressService addressService;

    @Autowired
    CouponService couponService;

    @Autowired
    BookService bookService;

    private static final String stock_prefix="stock_";//这个用来设置锁

    private static final String book_stock="book_stock_";//这个用来存储库存的缓存

    private static final String book_prefix="bookStore_book_";//这个用来存储单个图书的数据

    private static final String bookList_prefix="bookStore_bookList";//图书集合中数据

    public String initOrderId() {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
        String newDate=sdf.format(new Date());
        String result="";
        Random random=new Random();
        for(int i=0;i<6;i++){
            result+=random.nextInt(10);
        }
        return newDate+result;
    }


    @Override
    public OrderInitDto initSpikeOrder(String orderId, String account) {
        // 1. 根据订单ID查询订单信息
        OrderDto orderDto = orderMapper.findOrderDtoByOrderId(orderId);
        if (orderDto == null) {
            throw new RuntimeException("订单不存在");
        }

        // 2. 查询订单明细
        List<OrderDetailDto> orderDetailList = orderMapper.findOrderDetailDtoList(orderId);
        if (orderDetailList == null || orderDetailList.isEmpty()) {
            throw new RuntimeException("订单明细不存在");
        }

        // 3. 查询费用信息
        Expense expense = expenseMapper.findExpenseByOrderId(orderId);
        if (expense == null) {
            throw new RuntimeException("订单费用信息不存在");
        }

        // 4. 查询用户地址列表
        List<Address> addressList = addressService.addressList(account);

        // 5. 转换订单明细为图书列表格式
        List<OrderBookDto> bookList = new ArrayList<>();
        for (OrderDetailDto detail : orderDetailList) {
            OrderBookDto bookDto = new OrderBookDto();
            bookDto.setId(detail.getBook().getId());
            bookDto.setBookName(detail.getBook().getBookName());
            bookDto.setAuthor(detail.getBook().getAuthor());
            bookDto.setIsbn(detail.getBook().getisbn());
            bookDto.setPublish(detail.getBook().getPublish());
            bookDto.setBirthday(detail.getBook().getBirthday());
            bookDto.setMarketPrice(detail.getBook().getMarketPrice());
            bookDto.setPrice(detail.getPrice());
            bookDto.setStock(detail.getBook().getStock());
            bookDto.setDescription(detail.getBook().getDescription());
            bookDto.setNum(Integer.parseInt(detail.getNum()));

            // 单独查询并设置封面图片
            String coverImg = bookService.getBookCover(detail.getBook().getisbn());
            bookDto.setCoverImg(coverImg);

            bookList.add(bookDto);
        }

        // 6. 构建OrderInitDto
        OrderInitDto orderInitDto = new OrderInitDto();
        orderInitDto.setAccount(account);
        orderInitDto.setBookList(bookList);
        orderInitDto.setAddressList(addressList);
        orderInitDto.setExpense(expense);

        // 7. 设置默认地址
        if (addressList != null && !addressList.isEmpty()) {
            orderInitDto.setAddress(addressList.get(0));
        }

        return orderInitDto;
    }

    @Transactional
    @Override
    public boolean addOrder(OrderInitDto orderInitDto) {
        Order order = new Order();
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        String orderId = initOrderId();
        order.setOrderId(orderId);
        System.out.println("============orderInitDto.getAccount():==========="+orderInitDto.getAccount()+"============");
        order.setAccount(orderInitDto.getAccount());
        order.setAddressId(orderInitDto.getAddress().getId());//收货地址编号
        order.setOrderTime(timestamp);
        order.setOrderStatus(OrderStatusEnum.PENDING_PAYMENT.getValue());

        // 1. 先检查所有商品库存是否充足（不扣减）
        for(OrderBookDto orderBookDto : orderInitDto.getBookList()){
            Book book = bookMapper.getBook(orderBookDto.getId());
            if (book == null) {
                System.err.println("商品不存在：ID=" + orderBookDto.getId());
                return false;
            }
            if (book.getStock() < orderBookDto.getNum()) {
                System.err.println("库存不足：" + book.getBookName() +
                    "，当前库存：" + book.getStock() + "，需要：" + orderBookDto.getNum());
                return false;
            }
        }

        // 2. 创建订单详情
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for(OrderBookDto orderBookDto : orderInitDto.getBookList()){
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setBookId(orderBookDto.getId());
            orderDetail.setNum(orderBookDto.getNum());
            orderDetail.setPrice(orderBookDto.getPrice());
            orderDetail.setOrderId(orderId);
            orderDetailList.add(orderDetail);
            System.out.println("=====orderDetail.toString()====="+orderDetail.toString());
        }
        for(int i=0;i<orderDetailList.size();i++){
            System.out.println("=====orderDetailList[i]====="+orderDetailList.get(i));
        }
        // 2. 处理优惠券（如果不是秒杀订单且提供了优惠券码）
        if (!orderInitDto.isSpikeOrder() && orderInitDto.getCouponCode() != null && !orderInitDto.getCouponCode().trim().isEmpty()) {
            try {
                System.out.println("开始处理优惠券: " + orderInitDto.getCouponCode());

                // 计算优惠券折扣
                CouponCalculationResult couponResult = couponService.calculateCouponDiscount(
                    orderInitDto.getCouponCode(),
                    orderInitDto.getAccount(),
                    BigDecimal.valueOf(orderInitDto.getExpense().getProductTotalMoney())
                );

                if (couponResult.getAvailable()) {
                    // 更新费用信息
                    Expense expense = orderInitDto.getExpense();
                    expense.setCoupon(couponResult.getDiscountAmount().intValue());
                    expense.setCouponDiscount(couponResult.getDiscountAmount());
                    expense.setCouponId(couponResult.getCouponInfo().getId());
                    expense.setFinallyPrice(couponResult.getFinalAmount().doubleValue());

                    // 设置订单使用的优惠券ID
                    order.setCouponId(couponResult.getCouponInfo().getId());

                    System.out.println("优惠券计算成功: 原价=" + couponResult.getOriginalAmount() +
                        ", 优惠=" + couponResult.getDiscountAmount() +
                        ", 实付=" + couponResult.getFinalAmount());
                } else {
                    System.err.println("优惠券不可用: " + couponResult.getReason());
                    throw new RuntimeException("优惠券不可用: " + couponResult.getReason());
                }
            } catch (Exception e) {
                System.err.println("优惠券处理失败: " + e.getMessage());
                throw new RuntimeException("优惠券处理失败: " + e.getMessage());
            }
        }

        System.out.println("=============初始化总的订单没有问题===========");
        orderMapper.addOrder(order);//添加总的订单
        System.out.println("============添加总的订单成功============");

        orderMapper.batchAddOrderDetail(orderDetailList);//批量添加订单明细
        System.out.println("==============添加订单明细成功==============");

        // 3. 处理优惠券（在保存费用之前）
        Expense expense = orderInitDto.getExpense();
        expense.setOrderId(orderId);

        if (!orderInitDto.isSpikeOrder() && orderInitDto.getCouponCode() != null && !orderInitDto.getCouponCode().trim().isEmpty()) {
            try {
                // 计算优惠券折扣
                CouponCalculationResult couponResult = couponService.calculateCouponDiscount(
                    orderInitDto.getCouponCode(),
                    orderInitDto.getAccount(),
                    BigDecimal.valueOf(expense.getProductTotalMoney())
                );

                if (couponResult.getAvailable()) {
                    // 更新费用信息
                    int couponIntValue = couponResult.getDiscountAmount().intValue();
                    expense.setCoupon(couponIntValue);
                    expense.setCouponDiscount(couponResult.getDiscountAmount());
                    expense.setCouponId(couponResult.getCouponInfo().getId());
                    expense.setFinallyPrice(couponResult.getFinalAmount().doubleValue());

                    // 设置订单使用的优惠券ID
                    order.setCouponId(couponResult.getCouponInfo().getId());

                    System.out.println("优惠券计算成功: 原价=" + couponResult.getOriginalAmount() +
                        ", 优惠=" + couponResult.getDiscountAmount() +
                        ", 优惠int=" + couponIntValue +
                        ", 实付=" + couponResult.getFinalAmount());
                } else {
                    System.err.println("优惠券不可用: " + couponResult.getReason());
                    throw new RuntimeException("优惠券不可用: " + couponResult.getReason());
                }
            } catch (Exception e) {
                System.err.println("优惠券计算异常: " + e.getMessage());
                throw new RuntimeException("优惠券处理失败: " + e.getMessage());
            }
        }

        // 4. 保存费用信息（包含优惠券信息）
        expenseMapper.addExpense(expense);
        System.out.println("===========添加订单费用成功==============");

        // 5. 创建库存预留记录
        try {
            reserveStock(orderInitDto.getBookList(), orderId);
            System.out.println("库存预留完成: 订单ID=" + orderId);
        } catch (Exception e) {
            System.err.println("库存预留失败，回滚订单: " + e.getMessage());
            // 如果库存预留失败，需要删除已创建的订单
            orderMapper.delOrder(order.getId());
            return false;
        }

        // 6. 标记优惠券为已使用（如果有）
        if (!orderInitDto.isSpikeOrder() && orderInitDto.getCouponCode() != null && !orderInitDto.getCouponCode().trim().isEmpty()) {
            try {
                System.out.println("开始标记优惠券为已使用: 优惠券=" + orderInitDto.getCouponCode() + ", 订单ID=" + orderId);

                // 使用已计算的折扣金额
                BigDecimal discountAmount = expense.getCouponDiscount();
                if (discountAmount == null) {
                    discountAmount = BigDecimal.valueOf(expense.getCoupon());
                }

                boolean couponMarked = couponService.markCouponAsUsed(
                    orderInitDto.getCouponCode(),
                    orderInitDto.getAccount(),
                    orderId,
                    discountAmount
                );

                if (couponMarked) {
                    System.out.println("优惠券标记成功: 订单ID=" + orderId + ", 优惠券=" + orderInitDto.getCouponCode());
                } else {
                    System.err.println("优惠券标记失败: 订单ID=" + orderId + ", 优惠券=" + orderInitDto.getCouponCode());
                    // 优惠券标记失败不回滚订单，但记录日志
                }
            } catch (Exception e) {
                System.err.println("优惠券标记异常: 订单ID=" + orderId + ", 优惠券=" + orderInitDto.getCouponCode() + ", 异常=" + e.getMessage());
                e.printStackTrace();
                // 优惠券标记失败不回滚订单，但记录日志
            }
        }

        return true;
    }

    @Override
    @Transactional
    public int delOrder(int id) {
        // 1. 获取订单信息
        OrderDto orderDto = orderMapper.findOrderDto(id);
        if (orderDto == null) {
            System.out.println("删除订单失败：订单不存在, ID=" + id);
            return 0;
        }

        // 2. 如果是待支付订单，需要释放库存预留
        if ("待付款".equals(orderDto.getOrderStatus())) {
            System.out.println("删除待支付订单，释放库存预留: 订单ID=" + orderDto.getOrderId());
            try {
                releaseReservedStock(orderDto.getOrderId());
            } catch (Exception e) {
                System.err.println("释放库存预留失败: " + e.getMessage());
                // 即使释放库存失败，也继续删除订单，避免数据不一致
            }
        }

        // 3. 删除订单记录
        int result = orderMapper.delOrder(id);
        if (result > 0) {
            System.out.println("订单删除成功: ID=" + id + ", 订单号=" + orderDto.getOrderId());
        }
        return result;
    }

    @Override
    public int userDelOrder(int id) {
        Order order = new Order();
        order.setId(id);
        order.setBeUserDelete(true);
        return orderMapper.modifyOrder(order);
    }

    @Override
    public int batchDelOrder(List<Integer> item) {
        return orderMapper.batchDelOrder(item);
    }

    @Override
    @Transactional
    public int modifyOrderStatus(int id, String orderStatus) {
        // 1. 获取当前订单信息
        OrderDto orderDto = orderMapper.findOrderDto(id);
        if (orderDto == null) {
            System.err.println("修改订单状态失败：订单不存在, ID=" + id);
            return 0;
        }

        String currentStatus = orderDto.getOrderStatus();
        System.out.println("修改订单状态: ID=" + id + ", " + currentStatus + " -> " + orderStatus);

        // 2. 如果是从"待付款"状态取消订单，需要释放库存预留和优惠券
        if ("待付款".equals(currentStatus) && "已取消".equals(orderStatus)) {
            System.out.println("取消待支付订单，释放库存预留: 订单ID=" + orderDto.getOrderId());
            try {
                releaseReservedStock(orderDto.getOrderId());
                System.out.println("库存预留释放成功: 订单ID=" + orderDto.getOrderId());
            } catch (Exception e) {
                System.err.println("释放库存预留失败: " + e.getMessage());
                // 即使释放库存失败，也继续修改订单状态，避免用户无法取消订单
            }

            // 释放优惠券（如果有使用）
            try {
                boolean couponReleased = couponService.releaseCouponByOrderId(orderDto.getOrderId());
                if (couponReleased) {
                    System.out.println("优惠券释放成功: 订单ID=" + orderDto.getOrderId());
                }
            } catch (Exception e) {
                System.err.println("释放优惠券失败: " + e.getMessage());
                // 即使释放优惠券失败，也继续修改订单状态
            }
        }

        // 3. 更新订单状态
        Order order = new Order();
        order.setId(id);
        order.setOrderStatus(orderStatus);
        int result = orderMapper.modifyOrder(order);

        if (result > 0) {
            System.out.println("订单状态修改成功: ID=" + id + ", 新状态=" + orderStatus);
        }

        return result;
    }

    @Transactional
    @Override
    public int deliverBook(int id, int logisticsCompany, String logisticsNum) {
        int result = orderMapper.modifyLogistics(id, logisticsCompany, logisticsNum);
        Order order = new Order();
        order.setId(id);
        order.setOrderStatus("已发货");
        orderMapper.modifyOrder(order);
        return 1;
    }

    @Override
    public OrderDto findOrderDto(int id) {
        OrderDto orderDto = new OrderDto();
        orderDto = orderMapper.findOrderDto(id);
        List<OrderDetailDto> orderDetailDtoList  = orderMapper.findOrderDetailDtoList(orderDto.getOrderId());
        for(int i=0;i<orderDetailDtoList.size();i++){
            System.out.println("======="+orderDetailDtoList.get(i).toString()+"=====");
        }
        orderDto.setOrderDetailDtoList(orderDetailDtoList);
        return orderDto;
    }

    @Override
    public OrderDto findOrderDtoByOrderId(String orderId) {
        return orderMapper.findOrderDtoByOrderId(orderId);
    }

    @Override
    public List<OrderDetailDto> findOrderDetailDtoList(String orderId) {
        return orderMapper.findOrderDetailDtoList(orderId);
    }

    @Override
    public List<OrderDto> orderDtoList(String userId, int pageNum, int pageSize,String orderStatus,boolean beUserDelete) {
        pageNum = (pageNum-1)*pageSize;
        return orderMapper.orderDtoList(userId, pageNum, pageSize,orderStatus,beUserDelete);
    }

    @Override
    public int count(String userId,String orderStatus,boolean beUserDelete) {
        return orderMapper.count(userId,orderStatus,beUserDelete);
    }

    @Override
    public List<OrderStatistic> getOrderStatistic(String beginDate, String endDate) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = format.parse(beginDate);
        Date date1 = format.parse(endDate);

        return orderMapper.getOrderStatistic(new Timestamp(date.getTime()), new Timestamp(date1.getTime()));
    }

    @Override
    @Transactional
    public boolean confirmPayment(String orderId) {
        try {
            // 1. 查找订单
            OrderDto orderDto = orderMapper.findOrderDtoByOrderId(orderId);
            if (orderDto == null) {
                System.err.println("支付确认失败：订单不存在, 订单ID=" + orderId);
                return false;
            }

            // 2. 检查订单状态
            if (!"待付款".equals(orderDto.getOrderStatus())) {
                System.err.println("支付确认失败：订单状态不正确, 当前状态=" + orderDto.getOrderStatus());
                return false;
            }

            // 3. 确认库存扣减（将预留转为确认）
            confirmStockReduction(orderId);
            System.out.println("库存预留确认完成: 订单ID=" + orderId);

            // 4. 更新订单状态为"已付款"
            int result = modifyOrderStatus(orderDto.getId(), "已付款");
            if (result > 0) {
                System.out.println("支付确认成功，订单状态已更新为已付款: 订单ID=" + orderId);
                return true;
            } else {
                System.err.println("更新订单状态失败: 订单ID=" + orderId);
                return false;
            }

        } catch (Exception e) {
            System.err.println("支付确认失败: 订单ID=" + orderId + ", 错误=" + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Order> findTimeoutPendingOrders(int timeoutMinutes) {
        try {
            List<Order> timeoutOrders = orderMapper.findTimeoutPendingOrders(timeoutMinutes);
            System.out.println("查找到 " + timeoutOrders.size() + " 个超时订单");
            return timeoutOrders;
        } catch (Exception e) {
            System.err.println("查找超时订单失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public void reserveStock(List<OrderBookDto> bookList, String orderId) {
        System.out.println("开始预留库存: 订单ID=" + orderId + ", 商品数量=" + bookList.size());

        for (OrderBookDto book : bookList) {
            try {
                // 1. 检查库存是否充足
                Book bookInfo = bookMapper.getBook(book.getId());
                if (bookInfo == null) {
                    throw new RuntimeException("商品不存在：ID=" + book.getId());
                }

                if (bookInfo.getStock() < book.getNum()) {
                    throw new RuntimeException("库存不足：" + bookInfo.getBookName() +
                        "，当前库存：" + bookInfo.getStock() + "，需要：" + book.getNum());
                }

                // 2. 创建库存预留记录
                StockReservation reservation = new StockReservation();
                reservation.setBookId(book.getId());
                reservation.setOrderId(orderId);
                reservation.setReservedQuantity(book.getNum());
                reservation.setStatus(0); // 预留中

                int result = stockReservationMapper.insert(reservation);
                if (result <= 0) {
                    throw new RuntimeException("创建库存预留记录失败：" + bookInfo.getBookName());
                }

                // 3. 扣减可用库存（实际库存）
                int stockResult = bookMapper.modifyBookStock(book.getId(), book.getNum());
                if (stockResult <= 0) {
                    throw new RuntimeException("扣减库存失败：" + bookInfo.getBookName());
                }

                // 4. 更新Redis缓存
                updateStockInCache(book.getId(), book.getNum());

                System.out.println("库存预留成功: 商品ID=" + book.getId() + ", 预留数量=" + book.getNum());

            } catch (Exception e) {
                System.err.println("预留库存失败: 商品ID=" + book.getId() + ", 错误=" + e.getMessage());
                throw new RuntimeException("预留库存失败：" + e.getMessage());
            }
        }

        System.out.println("库存预留完成: 订单ID=" + orderId);
    }

    @Override
    @Transactional
    public void releaseReservedStock(String orderId) {
        System.out.println("开始释放预留库存: 订单ID=" + orderId);

        try {
            // 1. 查找该订单的库存预留记录
            List<StockReservation> reservations = stockReservationMapper.findByOrderId(orderId);

            if (reservations.isEmpty()) {
                System.out.println("未找到库存预留记录: 订单ID=" + orderId);
                return;
            }

            for (StockReservation reservation : reservations) {
                if (reservation.getStatus() == 0) { // 预留中
                    try {
                        // 2. 恢复图书库存
                        int restoreResult = bookMapper.restoreBookStock(reservation.getBookId(),
                            reservation.getReservedQuantity());

                        if (restoreResult > 0) {
                            // 3. 更新预留记录状态为已释放
                            reservation.setStatus(2);
                            stockReservationMapper.updateById(reservation);

                            // 4. 更新Redis缓存
                            restoreStockInCache(reservation.getBookId(), reservation.getReservedQuantity());

                            System.out.println("库存释放成功: 图书ID=" + reservation.getBookId() +
                                ", 释放数量=" + reservation.getReservedQuantity());
                        } else {
                            System.err.println("恢复库存失败: 图书ID=" + reservation.getBookId() +
                                ", 数量=" + reservation.getReservedQuantity());
                        }

                    } catch (Exception e) {
                        System.err.println("释放单个库存预留失败: 图书ID=" + reservation.getBookId() +
                            ", 错误=" + e.getMessage());
                    }
                }
            }

            System.out.println("库存预留释放完成: 订单ID=" + orderId);

        } catch (Exception e) {
            System.err.println("释放预留库存失败: 订单ID=" + orderId + ", 错误=" + e.getMessage());
            throw new RuntimeException("释放预留库存失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void confirmStockReduction(String orderId) {
        System.out.println("开始确认库存扣减: 订单ID=" + orderId);

        try {
            // 查找该订单的库存预留记录
            List<StockReservation> reservations = stockReservationMapper.findByOrderId(orderId);

            if (reservations.isEmpty()) {
                System.out.println("未找到库存预留记录: 订单ID=" + orderId);
                return;
            }

            for (StockReservation reservation : reservations) {
                if (reservation.getStatus() == 0) { // 预留中
                    // 更新预留记录状态为已确认
                    reservation.setStatus(1);
                    stockReservationMapper.updateById(reservation);

                    System.out.println("库存扣减确认: 图书ID=" + reservation.getBookId() +
                        ", 数量=" + reservation.getReservedQuantity());
                }
            }

            System.out.println("库存扣减确认完成: 订单ID=" + orderId);

        } catch (Exception e) {
            System.err.println("确认库存扣减失败: 订单ID=" + orderId + ", 错误=" + e.getMessage());
            throw new RuntimeException("确认库存扣减失败：" + e.getMessage());
        }
    }

    @Override
    public boolean canTransitionTo(String currentStatus, String targetStatus) {
        return false;
    }

    @Override
    public Map<String, Object> batchOperateOrders(List<Integer> ids, String operation) {
        Map<String, Object> result = new HashMap<>();
        try {
            int successCount = 0;
            int failCount = 0;

            for (Integer id : ids) {
                try {
                    switch (operation) {
                        case "delete":
                            delOrder(id);
                            successCount++;
                            break;
                        case "cancel":
                            modifyOrderStatus(id, "已取消");
                            successCount++;
                            break;
                        default:
                            failCount++;
                            break;
                    }
                } catch (Exception e) {
                    failCount++;
                    System.err.println("批量操作订单失败: ID=" + id + ", 错误=" + e.getMessage());
                }
            }

            result.put("success", true);
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("message", "批量操作完成：成功" + successCount + "个，失败" + failCount + "个");

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量操作失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 更新缓存中的库存（扣减）
     */
    private void updateStockInCache(int bookId, int quantity) {
        try {
            if(redisTemplate.hasKey(book_stock + bookId)){
                int currentStock = Integer.parseInt((String) redisTemplate.opsForValue().get(book_stock + bookId));
                int newStock = currentStock - quantity;
                redisTemplate.opsForValue().set(book_stock + bookId, String.valueOf(newStock));

                // 更新缓存中的图书信息
                if(redisTemplate.hasKey(book_prefix + bookId)){
                    ValueOperations<String, Book> operations = redisTemplate.opsForValue();
                    Book book = operations.get(book_prefix + bookId);
                    if(book != null) {
                        book.setStock(newStock);
                        redisTemplate.opsForValue().set(book_prefix + bookId, book);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("更新缓存库存失败: 图书ID=" + bookId + ", 错误=" + e.getMessage());
        }
    }

    /**
     * 恢复缓存中的库存（增加）
     */
    private void restoreStockInCache(int bookId, int quantity) {
        try {
            if(redisTemplate.hasKey(book_stock + bookId)){
                int currentStock = Integer.parseInt((String) redisTemplate.opsForValue().get(book_stock + bookId));
                int newStock = currentStock + quantity;
                redisTemplate.opsForValue().set(book_stock + bookId, String.valueOf(newStock));

                // 更新缓存中的图书信息
                if(redisTemplate.hasKey(book_prefix + bookId)){
                    ValueOperations<String, Book> operations = redisTemplate.opsForValue();
                    Book book = operations.get(book_prefix + bookId);
                    if(book != null) {
                        book.setStock(newStock);
                        redisTemplate.opsForValue().set(book_prefix + bookId, book);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("恢复缓存库存失败: 图书ID=" + bookId + ", 错误=" + e.getMessage());
        }
    }
}
