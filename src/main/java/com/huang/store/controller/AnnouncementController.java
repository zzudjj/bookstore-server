package com.huang.store.controller;

import com.huang.store.entity.notice.Announcement;
import com.huang.store.service.imp.AnnouncementService;
import com.huang.store.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@ResponseBody
@RequestMapping("/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    /**
     * 新增公告
     */
    @PostMapping("/addAnnouncement")
    public Map<String, Object> addAnnouncement(@RequestBody Announcement announcement) {
        if (announcementService.addAnnouncement(announcement) > 0) {
            return ResultUtil.resultCode(200, "添加公告成功");
        }
        return ResultUtil.resultCode(500, "添加公告失败");
    }

    /**
     * 删除公告
     */
    @GetMapping("/delAnnouncement")
    public Map<String, Object> delAnnouncement(@RequestParam("id") int id) {
        if (announcementService.deleteAnnouncement(id) > 0) {
            return ResultUtil.resultCode(200, "删除公告成功");
        }
        return ResultUtil.resultCode(500, "删除公告失败");
    }

    /**
     * 修改公告
     */
    @PostMapping("/modifyAnnouncement")
    public Map<String, Object> modifyAnnouncement(@RequestBody Announcement announcement) {
        if (announcementService.modifyAnnouncement(announcement) > 0) {
            return ResultUtil.resultCode(200, "修改公告成功");
        }
        return ResultUtil.resultCode(500, "修改公告失败");
    }

    /**
     * 获取单条公告
     */
    @GetMapping("/getAnnouncement")
    public Map<String, Object> getAnnouncement(@RequestParam("id") int id) {
        Map<String, Object> map = new HashMap<>();
        map.put("announcement", announcementService.getAnnouncement(id));
        return ResultUtil.resultSuccess(map);
    }

    /**
     * 分页查询公告
     */
    @GetMapping("/getAnnouncementList")
    public Map<String, Object> getAnnouncementList(@RequestParam("page") int page,
                                                   @RequestParam("pageSize") int pageSize) {
        Map<String, Object> map = new HashMap<>();
        List<Announcement> list = announcementService.getAnnouncementList(page, pageSize);
        map.put("announcementList", list);
        map.put("total", announcementService.getAnnouncementCount());
        return ResultUtil.resultSuccess(map);
    }

    /**
     * 获取所有启用公告（用户端）
     */
    @GetMapping("/getEnabledList")
    public Map<String, Object> getEnabledList() {
        Map<String, Object> map = new HashMap<>();
        map.put("announcementList", announcementService.getEnabledAnnouncements());
        return ResultUtil.resultSuccess(map);
    }
} 