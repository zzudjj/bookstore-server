package com.huang.store.controller;

import com.huang.store.configure.FileUploadConfig;
import com.huang.store.entity.book.Book;
import com.huang.store.entity.book.BookTopic;
import com.huang.store.entity.book.SubBookTopic;
import com.huang.store.entity.dto.TopicBook;
import com.huang.store.service.imp.BookService;
import com.huang.store.service.imp.TopicService;
import com.huang.store.util.FileUtil;
import com.huang.store.util.ResultUtil;
import com.huang.store.util.UploadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Controller
@ResponseBody
@RequestMapping(value = "/topic")
public class TopicController {

    private static final Logger logger = LoggerFactory.getLogger(TopicController.class);

    @Autowired
    private FileUploadConfig fileUploadConfig;

    @Qualifier("firstTopic")
    @Autowired
    TopicService topicService;

    @Autowired
    @Qualifier("firstVersion")
    BookService bookService;

    /**
     * 添加书单
     * @param map
     * @param file
     * @return
     */
    @PostMapping("/addTopic")
    public Map<String,Object> addTopic(@RequestParam Map<String,String> map,@RequestParam("file") MultipartFile file){
        BookTopic bookTopic = new BookTopic();
        bookTopic.setTopicName(map.get("topicName"));
        bookTopic.setRank(Integer.parseInt(map.get("rank")));
        bookTopic.setSubTitle(map.get("subTitle"));
        bookTopic.setPut(Boolean.valueOf(map.get("put")));
        logger.info("添加书单, put={}, bookTopic={}", map.get("put"), bookTopic);
        String path = fileUploadConfig.getTopicUploadPath();
        String imgName = UploadUtil.uploadFile(file,path);
        bookTopic.setCover(fileUploadConfig.getTopicPath()+imgName);
        if(topicService.addBookTopic(bookTopic)>0){
            return ResultUtil.resultCode(200,"添加书单成功");
        }
        return ResultUtil.resultCode(500,"添加书单成功");
    }

    /**
     * 修改书单
     * @param bookTopic
     * @return
     */
    @PostMapping("/modifyTopic")
    public Map<String,Object> modifyTopic(@RequestBody BookTopic bookTopic){
        logger.info("修改书单: {}", bookTopic);
        if(topicService.modifyBookTopic(bookTopic)>0){
            return ResultUtil.resultCode(200,"修改书单成功");
        }
        return ResultUtil.resultCode(500,"修改书单成功");
    }

    /**
     * 删除书单
     * @param url
     * @param id
     * @return
     */
    @GetMapping("/delTopicImg")
    public Map<String,Object> delImg(@RequestParam(value = "url") String url,
                                     @RequestParam(value = "id")int id){
        logger.info("删除图片");
        String path = fileUploadConfig.getFullPath(url);
        logger.info("删除的图片的路径是：{}", path);
        FileUtil.delOneImg(path);
        BookTopic topic = new BookTopic();
        topic.setId(id);
        topic.setCover("无");
        logger.info("更新书单封面为空: {}", topic);
        if(topicService.modifyBookTopic(topic)>0){
            return ResultUtil.resultCode(200,"删除图片成功");
        }
        return ResultUtil.resultCode(500,"删除图片失败");
    }

    /**
     * 上传书单图片
     * @param map
     * @param file
     * @return
     */
    @RequestMapping(value = "/uploadTopicImg")
    public Map<String,Object> upload(@RequestParam Map<String,String> map,@RequestParam("file") MultipartFile file) {
        Map<String,Object> m = new HashMap<>();
        int id = Integer.parseInt(map.get("id"));
        BookTopic bookTopic = new BookTopic();
        bookTopic.setId(id);
        String imgName = UploadUtil.uploadFile(file,fileUploadConfig.getTopicUploadPath());
        bookTopic.setCover(fileUploadConfig.getTopicPath()+imgName);
        if(topicService.modifyBookTopic(bookTopic)>0){
            return ResultUtil.resultCode(200,"添加图片成功");
        }
        return ResultUtil.resultCode(500,"删除图片失败");

    }

    /**
     * 修改书单排序
     * @param rank
     * @param id
     * @return
     */
    @GetMapping("/modifyTopicRank")
    public Map<String,Object> modifyRank(@RequestParam(value = "rank") int rank,
                                          @RequestParam(value = "id")int id){
        logger.info("开始修改排序, rank={}", rank);
        BookTopic bookTopic = new BookTopic();
        bookTopic.setRank(rank);
        bookTopic.setId(id);
        if(topicService.modifyBookTopic(bookTopic)>0){
            return ResultUtil.resultCode(200,"修改排序成功");
        }
        return ResultUtil.resultCode(500,"修改排序书单成功");
    }


    /**
     * 修改书单的上下架
     * @param put
     * @param id
     * @return
     */
    @GetMapping("/modifyTopicPut")
    public Map<String,Object> modifyPut(@RequestParam(value = "put") boolean put,
                                          @RequestParam(value = "id")int id){
        BookTopic bookTopic = new BookTopic();
        bookTopic.setPut(put);
        bookTopic.setId(id);
        if(topicService.modifyBookTopic(bookTopic)>0){
            return ResultUtil.resultCode(200,"修改成功");
        }
        return ResultUtil.resultCode(500,"修改成功");
    }

    /**
     * 删除书单
     * @param id
     * @return
     */
    @GetMapping("/delTopic")
    public Map<String,Object> delTopic(@RequestParam(value = "id")int id){
        logger.info("删除书单 id={}", id);
        if(topicService.delBookTopic(id)>0){
            return ResultUtil.resultCode(200,"删除书单成功");
        }
        return ResultUtil.resultCode(500,"删除书单失败");
    }

    /**
     * 得到书单的信息
     * @param id
     * @return
     */
    @GetMapping("/getTopic")
    public Map<String,Object> getTopic(@RequestParam(value = "id")int id){
        logger.info("获取书单详情 id={}", id);
        BookTopic bookTopic = topicService.getBookTopic(id);
        Map<String,Object> map = new HashMap<>();
        map.put("bookTopic",bookTopic);
        return ResultUtil.resultSuccess(map);
    }

    /**
     * 得到书单集合
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/getTopicList")
    public Map<String,Object> getTopicList(@RequestParam(value = "page")int page,
                                           @RequestParam(value = "pageSize")int pageSize){
        List<BookTopic> bookTopicList = topicService.getBookTopicList(page, pageSize);
        Map<String,Object> map = new HashMap<>();
        int total = topicService.getTopicCount();
        map.put("total",total);
        map.put("bookTopicList",bookTopicList);
        return ResultUtil.resultSuccess(map);
    }

    /**
     * 按页数得到所有未添加到某书单的所有图书
     * @param page
     * @param pageSize
     * @param topicId
     * @return
     */
    @GetMapping("/getNoAddBookList")
    public Map<String,Object> getTopicList(@RequestParam(value = "page")int page,
                                           @RequestParam(value = "pageSize")int pageSize,
                                           @RequestParam(value = "topicId")int topicId
                                           ){
        logger.info("请求未添加到书单的图书列表 topicId={}", topicId);
        List<Book> bookList = topicService.getNoAddBookPage(topicId,page, pageSize);
        for(Book book:bookList){
            book.setPut(false);
        }
        Map<String,Object> map = new HashMap<>();
        int total = topicService.getNoAddCount(topicId);
        map.put("total",total);
        map.put("bookList",bookList);
        return ResultUtil.resultSuccess(map);
    }

    /**
     * 添加图书到书单
     * @param id
     * @param bookId
     * @return
     */
    @GetMapping("/addSubTopic")
    public Map<String,Object> addSubTopic(@RequestParam(value = "id")int id,
                                          @RequestParam(value = "bookId")int bookId){
        logger.info("添加图书到书单 topicId={}, bookId={}", id, bookId);
        // logger.info("说明添加请求已经到添加图书到书单中");
        SubBookTopic subBookTopic = new SubBookTopic();
        subBookTopic.setTopicId(id);
        subBookTopic.setBookId(bookId);
        // logger.info(subBookTopic.toString());
        if(topicService.addSubBookTopic(subBookTopic)>0){
            return ResultUtil.resultCode(200,"添加图书到书单成功");
        }
        return ResultUtil.resultCode(500,"添加图书到书单成功");
    }

    /**
     * 从某个书单中删除某本图书
     * @param id
     * @param bookId
     * @return
     */
    @GetMapping("/delSubTopic")
    public Map<String,Object> delSubTopic(@RequestParam(value = "id")int id,
                                          @RequestParam(value = "bookId")int bookId){
        logger.info("删除书单图书 topicId={}, bookId={}", id, bookId);
        // logger.info("说明删除请求已经到删除图书的处理中");
        if(topicService.delSubBookTopic(id, bookId)>0){
            return ResultUtil.resultCode(200,"删除书单图书成功");
        }
        return ResultUtil.resultCode(500,"删除书单图书失败");
    }

    /**
     * 得到某本图书的推荐理由
     * @param topicId
     * @param bookId
     * @return
     */
    @GetMapping("/getReason")
    public Map<String,Object> getRecReason(@RequestParam(value = "topicId")int topicId,
                                          @RequestParam(value = "bookId")int bookId){
        // 获取推荐理由
        String reason = topicService.getReason(topicId, bookId);
        Map<String,Object> map = new HashMap<>();
        map.put("reason",reason);
        return ResultUtil.resultSuccess(map);
    }

    /**
     * 修改某书单的某本图书的推荐理由
     * @param id
     * @param bookId
     * @param recReason
     * @return
     */
    @GetMapping("/modifySubTopic")
    public Map<String,Object> delSubTopic(@RequestParam(value = "id")int id,
                                          @RequestParam(value = "bookId")int bookId,
                                          @RequestParam(value = "recReason")String recReason){
        SubBookTopic subBookTopic = new SubBookTopic();
        subBookTopic.setBookId(bookId);
        subBookTopic.setTopicId(id);
        subBookTopic.setRecommendReason(recReason);
        // 开始修改书单
        if(topicService.modifySubBookTopic(subBookTopic)>0){
            return ResultUtil.resultCode(200,"修改图书推荐理由成功");
        }
        return ResultUtil.resultCode(500,"删除图书推荐理由失败");
    }

    /**
     * 批量删除或者添加书单数据
     * @param ids
     * @param operator
     * @param topicId
     * @return
     */
    @PostMapping("/batchTopic")
    public Map<String,Object> batchTopic(@RequestParam(value = "ids") int[] ids,
                                         @RequestParam(value = "operator")String operator,
                                         @RequestParam(value = "topicId")int topicId){
        logger.info("批量操作 operator={}, ids={}", operator, Arrays.toString(ids));
        // logger.info("说明已经请求到了");
        // logger.info("批量操作请求到达");
        List<SubBookTopic> list = new ArrayList<>();
        for(int i=0;i<ids.length;i++){
            SubBookTopic bookTopic = new SubBookTopic();
            bookTopic.setTopicId(topicId);
            bookTopic.setBookId(ids[i]);
            list.add(bookTopic);
        }
        switch (operator){
            case "add":
                // logger.info("开始批量添加");
                topicService.batchAddSubTopic(list);
                break;
//            case "del":
//                topicService.batchDelSubTopic(list);
//                break;
            default:
                return ResultUtil.resultCode(500,"批量操作失败");
        }
        return ResultUtil.resultCode(200,"批量操作成功");
    }

    /**
     * 得到某书单的所有图书集合
     * @param topicId
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/getSubTopicList")
    public Map<String,Object> getSubTopicList(@RequestParam(value = "id")int topicId,
                                              @RequestParam(value = "page")int page,
                                              @RequestParam(value = "pageSize")int pageSize){
        List<Book> subBookTopicList = topicService.getSubBookTopicList(topicId, page, pageSize);
        Map<String,Object> map = new HashMap<>();
        int total = topicService.getSubTopicCount(topicId);
        map.put("total",total);
        map.put("subTopicList",subBookTopicList);
        return ResultUtil.resultSuccess(map);
    }

    /**
     * 得到所有添加到指定书单的所有图书
     * @param topicId
     * @return
     */
    @GetMapping("/getTopicBookList")
    public Map<String,Object> getSubTopicList(@RequestParam(value = "id")int topicId){
        List<TopicBook> TopicBookList = topicService.getTopicBookList(topicId);
        final String defaultCover = "/static/image/topic/default_topic_cover.svg";
        for (TopicBook tb : TopicBookList) {
            String img = bookService.getBookCover(tb.getIsbn());
            if (img == null || img.trim().isEmpty()) {
                img = defaultCover;
            }
            tb.setCoverImg(img);
        }
        BookTopic bookTopic = topicService.getBookTopic(topicId);
        Map<String,Object> map = new HashMap<>();
        map.put("TopicBookList",TopicBookList);
        map.put("total", TopicBookList.size());
        map.put("bookTopic",bookTopic);
        return ResultUtil.resultSuccess(map);
    }

}
