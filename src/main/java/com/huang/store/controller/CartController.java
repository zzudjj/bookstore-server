package com.huang.store.controller;

import com.huang.store.entity.dto.CartBookDto;
import com.huang.store.service.imp.BookService;
import com.huang.store.service.imp.CartService;
import com.huang.store.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: 黄龙
 * @date: 2020/7/23 20:18
 * @description:
 */
@Controller
@ResponseBody
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    @Qualifier("firstCart")
    CartService cartService;

    @Autowired
    @Qualifier("firstVersion")
    BookService bookService;

    /**
     * 添加图书到购物车
     * @param id 图书ID
     * @param num 数量
     * @param account 用户账号
     * @return 添加结果
     */
    @GetMapping("/addCart")
    public Map<String, Object> addCart(@RequestParam("id") int id,
                                      @RequestParam("num") int num,
                                      @RequestParam("account") String account) {
        logger.info("添加图书到购物车: 用户={}, 图书ID={}, 数量={}", account, id, num);

        // 参数验证
        if (account == null || account.trim().isEmpty()) {
            logger.warn("账号参数为空");
            return ResultUtil.resultCode(400, "账号不能为空");
        }
        if (id <= 0) {
            logger.warn("图书ID无效: {}", id);
            return ResultUtil.resultCode(400, "图书ID无效");
        }
        if (num <= 0) {
            logger.warn("数量无效: {}", num);
            return ResultUtil.resultCode(400, "数量必须大于0");
        }

        // 检查购物车中是否已存在该图书
        if (cartService.existProduct(account, id) > 0) {
            logger.warn("购物车中已存在该图书: 用户={}, 图书ID={}", account, id);
            return ResultUtil.resultCode(500, "购物车中已经存在该图书");
        }

        int result = cartService.addProduct(account, id, num);
        if (result <= 0) {
            logger.error("添加到购物车失败: 用户={}, 图书ID={}, 数量={}", account, id, num);
            return ResultUtil.resultCode(500, "添加到购物车失败");
        }

        logger.info("添加到购物车成功: 用户={}, 图书ID={}, 数量={}", account, id, num);
        return ResultUtil.resultCode(200, "添加到购物车成功");
    }

    /**
     * 删除某人购物车中的某本图书
     * @param id
     * @param account
     * @return
     */
    @GetMapping("/delCart")
    public Map<String,Object> deleteCart(@RequestParam("id")int id,
                                         @RequestParam("account")String account){
        if(cartService.deleteProduct(account,id)>0){
            return ResultUtil.resultCode(200,"删除成功");
        }
        return ResultUtil.resultCode(500,"删除失败");
    }

    /**
     * 批量删除购物车
     * @param ids
     * @param account
     * @return
     */
    @GetMapping("/batchDelCart")
    public Map<String,Object> batchDeleteCart(@RequestParam("ids")int[] ids,
                                              @RequestParam("account")String account){
        if(cartService.delBatchProduct(account,ids)>0){
            return ResultUtil.resultCode(200,"删除成功");
        }
        return ResultUtil.resultCode(500,"删除失败");
    }

    /**
     * 这里的修改购物车只是修改购物车的数量
     * @param id
     * @param num
     * @param account
     * @return
     */
    @GetMapping("/modifyCart")
    public Map<String,Object> modifyCart(@RequestParam("id")int id,
                                      @RequestParam("num")int num,
                                      @RequestParam("account")String account){
        if(cartService.modifyProductNum(account,id,num)>0){
            return ResultUtil.resultCode(200,"修改成功");
        }
        return ResultUtil.resultCode(500,"修改失败");
    }


    /**
     * 得到购物车图书列表
     * @param account
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/getCartList")
    public Map<String,Object> getCartList(@RequestParam("account")String account,
                                          @RequestParam("page")int page,
                                          @RequestParam("pageSize")int pageSize){
        Map<String,Object> map = new HashMap<>();
        List<CartBookDto> cartBookDtoList = cartService.getCartsByPage(account, page, pageSize);
        for(int i=0;i<cartBookDtoList.size();i++){
            String img = bookService.getBookCover(cartBookDtoList.get(i).getIsbn());
            cartBookDtoList.get(i).setCoverImg(img);
        }
        map.put("cartBookDtoList",cartBookDtoList);
        return ResultUtil.resultSuccess(map);
    }

}
