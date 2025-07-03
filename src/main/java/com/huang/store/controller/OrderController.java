package com.huang.store.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.huang.store.common.ApiResponse;
import com.huang.store.entity.dto.*;
import com.huang.store.entity.order.Expense;
import com.huang.store.entity.user.Address;
import com.huang.store.exception.BusinessException;
import com.huang.store.service.imp.AddressService;
import com.huang.store.service.imp.BookService;
import com.huang.store.service.imp.CartService;
import com.huang.store.service.imp.OrderService;
import com.huang.store.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: 黄龙
 * @date: 2020/7/22 20:25
 * @description:
 */
@Controller
@ResponseBody
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    @Qualifier("firstVersion")
    BookService bookService;

    @Autowired
    @Qualifier("firstCart")
    CartService cartService;

    @Autowired
    @Qualifier("firstAddress")
    AddressService addressService;

    @Autowired
    @Qualifier("orderService")
    OrderService orderService;

    /**
     * 初始化秒杀订单支付页面
     * @param orderId 已创建的订单ID
     * @param account 用户账号
     * @return
     */
    @GetMapping("/initSpikeOrder")
    public Map<String,Object> initSpikeOrder(@RequestParam(value = "orderId") String orderId,
                                             @RequestParam(value = "account") String account){
        Map<String,Object> map = new HashMap<>();
        try {
            OrderInitDto orderInitDto = orderService.initSpikeOrder(orderId, account);
            map.put("orderInitDto", orderInitDto);
            return ResultUtil.resultSuccess(map);
        } catch (Exception e) {
            return ResultUtil.resultCode(500, "初始化订单失败：" + e.getMessage());
        }
    }

    /**
     * 初始化订单，得到用户购买的图书集合，得到费用信息，并返回前端费用信息和图书集合
     * @param ids 用户预购买的时候的图书id集合
     * @param from from用来标记下订单是从哪个页面操作的 0代表详情页 1代表购物车
     * @return
     */
    @GetMapping("/initOrder")
    public Map<String,Object> initOrder(@RequestParam(value = "ids") int[] ids,
                                        @RequestParam(value = "from") int from,
                                        @RequestParam(value = "account") String account){
        logger.info("初始化订单: 用户={}, 商品IDs={}, 来源={}", account, Arrays.toString(ids), from);

        Map<String,Object> map = new HashMap<>();
        Expense expense = new Expense();
        OrderInitDto orderInitDto = new OrderInitDto();
        List<OrderBookDto> batchBookList = new ArrayList<>();

        try {
            if(from==1){//从购物车点击提交的
                // 一次性获取购物车中的商品信息（包含数量）
                batchBookList = bookService.getBatchBookList(ids, account);
                logger.info("从购物车获取到{}件商品", batchBookList.size());

                // 验证商品数据完整性
                if(batchBookList.isEmpty()) {
                    logger.warn("购物车中没有找到指定的商品");
                    return ResultUtil.resultCode(400, "购物车中没有找到指定的商品");
                }

                // 验证库存充足性
                for(OrderBookDto book : batchBookList) {
                    if(book.getStock() < book.getNum()) {
                        logger.warn("商品库存不足: 商品ID={}, 需要数量={}, 库存={}",
                                   book.getId(), book.getNum(), book.getStock());
                        return ResultUtil.resultCode(400, "商品《" + book.getBookName() + "》库存不足");
                    }
                }

                // 注意：这里不删除购物车，等订单确认后再删除

            }else {//从详情页点击提交的
                batchBookList = bookService.getOneBookList(ids);
                if(!batchBookList.isEmpty()) {
                    batchBookList.get(0).setNum(1);
                }
            }

            // 设置商品封面图片
            for(OrderBookDto book : batchBookList){
                String img = bookService.getBookCover(book.getIsbn());
                book.setCoverImg(img);
            }

            // 计算订单金额
            Double productTotalMoney = 0.0;
            for(OrderBookDto orderBookDto : batchBookList){
                productTotalMoney = productTotalMoney + orderBookDto.getPrice() * orderBookDto.getNum();
            }

            // 设置费用信息
            expense.setProductTotalMoney(productTotalMoney);//商品总价
            expense.setFreight(0);
            expense.setCoupon(0);
            expense.setActivityDiscount(0);
            expense.setAllPrice(productTotalMoney);//订单总金额
            expense.setFinallyPrice(productTotalMoney);//最终实付金额

            // 获取用户地址列表
            List<Address> addressList = addressService.addressList(account);
            if(addressList == null || addressList.isEmpty()) {
                logger.warn("用户没有配置收货地址: {}", account);
                return ResultUtil.resultCode(400, "请先添加收货地址");
            }

            // 构建订单初始化数据
            orderInitDto.setAddressList(addressList);
            orderInitDto.setBookList(batchBookList);
            orderInitDto.setExpense(expense);
            orderInitDto.setAccount(account);

            logger.info("订单初始化成功: 用户={}, 商品数量={}, 总金额={}",
                       account, batchBookList.size(), productTotalMoney);

            map.put("orderInitDto", orderInitDto);
            return ResultUtil.resultSuccess(map);

        } catch (Exception e) {
            logger.error("订单初始化失败: 用户={}, 错误={}", account, e.getMessage(), e);
            return ResultUtil.resultCode(500, "订单初始化失败: " + e.getMessage());
        }
    }

    /**
     * 添加订单
     * @param orderInitDto
     * @return
     */
    @PostMapping("/addOrder")
    public Map<String,Object> addOrder(@RequestBody OrderInitDto orderInitDto){
        logger.info("创建订单: 用户={}, 商品数量={}", orderInitDto.getAccount(),
                   orderInitDto.getBookList() != null ? orderInitDto.getBookList().size() : 0);

        try {
            // 创建订单
            boolean orderCreated = orderService.addOrder(orderInitDto);
            if(!orderCreated){
                logger.error("订单创建失败: 用户={}", orderInitDto.getAccount());
                return ResultUtil.resultCode(500,"提交订单失败");
            }

            // 订单创建成功后，删除购物车中的商品（仅针对购物车来源的订单）
            if(orderInitDto.getBookList() != null && !orderInitDto.getBookList().isEmpty()) {
                try {
                    // 提取商品ID数组
                    int[] bookIds = orderInitDto.getBookList().stream()
                            .mapToInt(OrderBookDto::getId)
                            .toArray();

                    // 删除购物车中的商品
                    int deletedCount = cartService.delBatchProduct(orderInitDto.getAccount(), bookIds);
                    logger.info("订单创建成功，已删除购物车商品: 用户={}, 删除数量={}",
                               orderInitDto.getAccount(), deletedCount);
                } catch (Exception e) {
                    // 购物车删除失败不影响订单创建
                    logger.warn("删除购物车商品失败，但订单已创建: 用户={}, 错误={}",
                               orderInitDto.getAccount(), e.getMessage());
                }
            }

            return ResultUtil.resultCode(200,"订单创建成功，请完成支付");

        } catch (Exception e) {
            logger.error("创建订单异常: 用户={}, 错误={}", orderInitDto.getAccount(), e.getMessage(), e);
            return ResultUtil.resultCode(500,"提交订单失败: " + e.getMessage());
        }
    }

    /**
     * 确认支付
     * @param request 包含订单ID的请求对象
     * @return
     */
    @PostMapping("/confirmPayment")
    public Map<String,Object> confirmPayment(@RequestBody Map<String, String> request){
        try {
            String orderId = request.get("orderId");
            if (orderId == null || orderId.trim().isEmpty()) {
                return ResultUtil.resultCode(400, "订单ID不能为空");
            }

            boolean result = orderService.confirmPayment(orderId);
            if (result) {
                return ResultUtil.resultCode(200, "支付成功");
            } else {
                return ResultUtil.resultCode(500, "支付失败");
            }
        } catch (Exception e) {
            return ResultUtil.resultCode(500, "支付失败：" + e.getMessage());
        }
    }


    /**
     * 得到管理员可以查看的订单信息列表
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/getAdminOrderList")
    public Map<String,Object> getOrderList(@RequestParam("page")int page,
                                       @RequestParam("pageSize")int pageSize,
                                       @RequestParam(value = "orderStatus", required = false, defaultValue = "")String orderStatus,
                                       @RequestParam(value = "orderId", required = false, defaultValue = "")String orderId,
                                       @RequestParam(value = "account", required = false, defaultValue = "")String account){
        logger.info("获取订单列表: page={}, pageSize={}, orderStatus={}, orderId={}, account={}",
                   page, pageSize, orderStatus, orderId, account);

        List<OrderDto> orderDtoList = orderService.orderDtoList("", page, pageSize, orderStatus, false);

        // 为每个订单添加图书封面信息
        for(OrderDto orderDto : orderDtoList){
            List<OrderDetailDto> orderDetailDtoList = orderService.findOrderDetailDtoList(orderDto.getOrderId());
            List<String> coverImgList = new ArrayList<>();
            for(int j=0; j<orderDetailDtoList.size() && j<5; j++){
                String img = bookService.getBookCover(orderDetailDtoList.get(j).getBook().getisbn());
                coverImgList.add(img);
            }
            orderDto.setCoverImgList(coverImgList);
            orderDto.setOrderDetailDtoList(orderDetailDtoList);
        }

        // 根据搜索条件过滤结果
        if (!orderId.isEmpty() || !account.isEmpty()) {
            orderDtoList = orderDtoList.stream()
                .filter(order ->
                    (orderId.isEmpty() || order.getOrderId().contains(orderId)) &&
                    (account.isEmpty() || order.getAccount().contains(account))
                )
                .collect(Collectors.toList());
        }

        Map<String,Object> map= new HashMap<>();
        map.put("orderDtoList",orderDtoList);
        int total = orderService.count("", orderStatus, false);
        map.put("total",total);
        return ResultUtil.resultSuccess(map);
    }

    /**
     * 得到某个订单的全部明细信息
     * @param id
     * @return
     */
    @GetMapping("/getOrderDto")
    public Map<String,Object> getOrderDto(@RequestParam("id")int id){
        OrderDto orderDto = orderService.findOrderDto(id);
        List<OrderDetailDto> orderDetailDtoList = orderService.findOrderDetailDtoList(orderDto.getOrderId());
        for(int i=0;i<orderDetailDtoList.size();i++){
            String img = bookService.getBookCover(orderDetailDtoList.get(i).getBook().getisbn());
            orderDetailDtoList.get(i).getBook().setCoverImg(img);
            System.out.println("=======orderDetailDtoList.size():====="+orderDetailDtoList.size()+"============");
        }
        orderDto.setOrderDetailDtoList(orderDetailDtoList);
        Map<String,Object> map= new HashMap<>();
        map.put("orderDto",orderDto);
        return ResultUtil.resultSuccess(map);
    }


    /**
     * 删除订单
     * @param id
     * @return
     */
    @GetMapping("/delOrder")
    public Map<String,Object> delOrder(@RequestParam("id")int id){
        logger.info("删除订单: id={}", id);
        if(orderService.delOrder(id)>0){
            return ResultUtil.resultCode(200,"删除订单成功");
        }
        return ResultUtil.resultCode(500,"删除订单失败");
    }

    @GetMapping("/deliverOrder")
    public Map<String,Object> deliverOrder(@RequestParam("id")int id,
                                      @RequestParam("logisticsCompany")int logisticsCompany,
                                      @RequestParam("logisticsNum")String logisticsNum){
        System.out.println("============="+id+"=================");
        if(orderService.deliverBook(id,logisticsCompany,logisticsNum)>0){
            return ResultUtil.resultCode(200,"发货成功");
        }
        return ResultUtil.resultCode(500,"发货失败");
    }

    @GetMapping("/getUserOrderList")
    public Map<String,Object> getUserOrderList(@RequestParam("account")String account,
                                               @RequestParam("page")int page,
                                               @RequestParam("pageSize")int pageSize,
                                               @RequestParam("orderStatus")String orderStatus,
                                               @RequestParam("beUserDelete")boolean beUserDelete){
        System.out.println("=========orderStatus,beUserDelete===========:"+orderStatus+" "+beUserDelete+"=========");
        List<OrderDto> orderDtoList = orderService.orderDtoList(account, page, pageSize,orderStatus,beUserDelete);
        for(int i=0;i<orderDtoList.size();i++){
            List<OrderDetailDto> orderDetailDtoList = orderService.findOrderDetailDtoList(orderDtoList.get(i).getOrderId());
            List<String> coverImgList = new ArrayList<>();
            for(int j=0;j<orderDetailDtoList.size() && j<5;j++){
                System.out.println("======orderDetailDtoList.get(j)====:"+orderDetailDtoList.get(j)+"=========");
                String img = bookService.getBookCover(orderDetailDtoList.get(j).getBook().getisbn());
                coverImgList.add(img);
            }
            System.out.println("=====coverImgList.size()====="+coverImgList.size()+"===================");
            orderDtoList.get(i).setCoverImgList(coverImgList);
        }
        Map<String,Object> map= new HashMap<>();
        map.put("orderDtoList",orderDtoList);
        int total = orderService.count(account,orderStatus,beUserDelete);
        map.put("total",total);
        return ResultUtil.resultSuccess(map);
    }

    /**
     * 修改订单的状态
     * @param id
     * @param orderStatus
     * @return
     */
    @GetMapping("/modifyOrderStatus")
    public Map<String,Object> modifyOrderStatus(@RequestParam("id")int id,
                                      @RequestParam("orderStatus")String orderStatus){
        System.out.println("========确认收货===="+id);

        // 简化状态验证
        try {
            if (!orderService.canTransitionTo("", orderStatus)) {
                System.out.println("状态转换验证");
            }
        } catch (Exception e) {
            System.out.println("状态验证异常：" + e.getMessage());
        }

        if(orderService.modifyOrderStatus(id,orderStatus)>0){
            return ResultUtil.resultCode(200,"操作成功");
        }
        return ResultUtil.resultCode(500,"操作失败");
    }

    /**
     * 管理员批量操作订单
     * @param params 包含ids和operation的参数
     * @return
     */
    @PostMapping("/admin/orders/batch")
    public Map<String, Object> batchOperateOrders(@RequestBody Map<String, Object> params) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> ids = (List<Integer>) params.get("ids");
            String operation = params.get("operation").toString();

            if (ids == null || ids.isEmpty()) {
                return ResultUtil.resultCode(400, "请选择要操作的订单");
            }

            Map<String, Object> result = orderService.batchOperateOrders(ids, operation);

            int successCount = (Integer) result.get("successCount");
            int failCount = (Integer) result.get("failCount");

            if (failCount == 0) {
                return ResultUtil.resultCode(200, "批量操作成功，共处理 " + successCount + " 个订单", result);
            } else {
                return ResultUtil.resultCode(206, "部分操作成功：成功 " + successCount + " 个，失败 " + failCount + " 个", result);
            }
        } catch (Exception e) {
            return ResultUtil.resultCode(500, "批量操作失败：" + e.getMessage());
        }
    }


    /**
     * 返回日期筛选后的订单数据
     * @param beginDate
     * @param endDate
     * @return
     */
    @RequestMapping(value = "/order/date", method = RequestMethod.GET)
    public Map<String,Object> dateFilter(@RequestParam("beginDate")String beginDate,
                                         @RequestParam("endDate")String endDate) throws ParseException {
        // 将结束日期+1 便于sql操作
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date0 = format.parse(beginDate);
        Date date1 = format.parse(endDate);
//        try {
////            Calendar calendar = Calendar.getInstance();
////            calendar.setTime(date1);
////            calendar.add(Calendar.DAY_OF_MONTH, 1);
////            date1 = calendar.getTime();
////        } catch (Exception e){
////            System.out.println(e);
////        }
        Map<String,Object> map = new HashMap<>();
        List<OrderStatistic> orderStatistic = orderService.getOrderStatistic(beginDate, endDate);
        map.put("orderStatistic",orderStatistic);
        return ResultUtil.resultSuccess(map);
    }


}
