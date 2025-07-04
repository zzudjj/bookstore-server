package com.huang.store.controller;

import com.huang.store.entity.notice.About;
import com.huang.store.service.imp.AboutService;
import com.huang.store.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@ResponseBody
@RequestMapping("/about")
public class AboutController {

    @Autowired
    private AboutService aboutService;

    /**
     * 获取网站介绍
     */
    @GetMapping("/get")
    public Map<String, Object> getAbout() {
        Map<String, Object> map = new HashMap<>();
        map.put("about", aboutService.getAbout());
        return ResultUtil.resultSuccess(map);
    }

    /**
     * 修改网站介绍
     */
    @PostMapping("/modify")
    public Map<String, Object> modify(@RequestBody About about) {
        if (aboutService.modifyAbout(about) > 0) {
            return ResultUtil.resultCode(200, "修改网站介绍成功");
        }
        return ResultUtil.resultCode(500, "修改网站介绍失败");
    }

    /**
     * 初始化添加网站介绍（仅管理员首次使用）
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody About about) {
        if (aboutService.addAbout(about) > 0) {
            return ResultUtil.resultCode(200, "添加网站介绍成功");
        }
        return ResultUtil.resultCode(500, "添加网站介绍失败");
    }
} 