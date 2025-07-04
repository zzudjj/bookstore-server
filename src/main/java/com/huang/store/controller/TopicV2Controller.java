package com.huang.store.controller;

import com.huang.store.entity.topic.Topic;
import com.huang.store.entity.topic.TopicItem;
import com.huang.store.service.imp.TopicV2Service;
import com.huang.store.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@ResponseBody
@RequestMapping("/topic/v2")
public class TopicV2Controller {

    @Autowired
    private TopicV2Service topicService;

    /* ==================== 前台接口 ==================== */
    @GetMapping("/list")
    public Map<String,Object> list(@RequestParam int page,@RequestParam int pageSize,@RequestParam(value="all",required=false,defaultValue="false") boolean all){
        List<Topic> topics;
        int total;
        if(all){
            topics = topicService.getTopicList(page,pageSize);
            total = topicService.getTopicCount();
        }else{
            topics = topicService.getPublicTopics(page,pageSize);
            total = topicService.getPublicTopicCount();
        }
        Map<String,Object> map=new HashMap<>();
        map.put("topicList",topics);
        map.put("total",total);
        return ResultUtil.resultSuccess(map);
    }

    @GetMapping("/{id}")
    public Map<String,Object> detail(@PathVariable int id){
        Topic topic = topicService.getTopic(id);
        List<TopicItem> items = topicService.getTopicItems(id);
        Map<String,Object> map = new HashMap<>();
        map.put("topic",topic);
        map.put("items",items);
        return ResultUtil.resultSuccess(map);
    }

    /* ==================== 管理端接口 ==================== */
    @PostMapping("/admin")
    public Map<String,Object> create(@RequestBody Topic topic){
        topicService.addTopic(topic, topic.getItems());
        Map<String, Object> data = new HashMap<>();
        data.put("id", topic.getId());
        return ResultUtil.resultCode(200, "创建书单成功", data);
    }

    @PutMapping("/admin/{id}")
    public Map<String,Object> update(@PathVariable int id,@RequestBody Topic topic){
        topic.setId(id);
        topicService.updateTopic(topic, topic.getItems());
        return ResultUtil.resultCode(200,"修改成功");
    }

    @DeleteMapping("/admin/{id}")
    public Map<String,Object> delete(@PathVariable int id){
        topicService.deleteTopic(id);
        return ResultUtil.resultCode(200,"删除成功");
    }

    @PutMapping("/admin/{id}/status")
    public Map<String,Object> changeStatus(@PathVariable int id,@RequestBody Map<String,Object> body){
        boolean status = Boolean.parseBoolean(String.valueOf(body.get("status")));
        topicService.changeTopicStatus(id,status);
        return ResultUtil.resultCode(200,"状态更新成功");
    }

    /* 上传/替换封面 */
    @PostMapping("/admin/{id}/cover")
    public Map<String,Object> uploadCover(@PathVariable int id,
                                          @RequestParam("file") MultipartFile file,
                                          com.huang.store.configure.FileUploadConfig fileUploadConfig){
        String path = fileUploadConfig.getTopicUploadPath();
        String imgName = com.huang.store.util.UploadUtil.uploadFile(file, path);
        // 读取当前书单，避免覆盖 rank/status 等字段
        Topic topic = topicService.getTopic(id);
        String relativePath = fileUploadConfig.getTopicPath() + imgName;
        if (!relativePath.startsWith("/")) {
            relativePath = "/" + relativePath;
        }
        topic.setCover(relativePath);
        topicService.updateTopic(topic, null);

        Map<String, Object> data = new HashMap<>();
        data.put("cover", topic.getCover());
        return ResultUtil.resultSuccess(data);
    }

} 