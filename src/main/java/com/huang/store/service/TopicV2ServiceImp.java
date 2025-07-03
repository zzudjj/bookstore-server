package com.huang.store.service;

import com.huang.store.entity.topic.Topic;
import com.huang.store.entity.topic.TopicItem;
import com.huang.store.mapper.TopicV2Mapper;
import com.huang.store.service.imp.TopicV2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("topicV2ServiceImpl")
public class TopicV2ServiceImp implements TopicV2Service {

    @Autowired
    private TopicV2Mapper mapper;

    @Autowired
    private com.huang.store.service.imp.BookService bookService;

    @Override
    public List<Topic> getTopicList(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return mapper.getTopicList(offset, pageSize);
    }

    @Override
    public int getTopicCount() {
        return mapper.getTopicCount();
    }

    @Override
    public Topic getTopic(int id) {
        return mapper.getTopic(id);
    }

    @Override
    public List<TopicItem> getTopicItems(int topicId) {
        List<TopicItem> items = mapper.getTopicItems(topicId);
        for (TopicItem it : items) {
            String cover = bookService.getBookCover(bookService.getBookIsbn(it.getBookId()));
            if (cover == null || cover.trim().isEmpty()) {
                cover = "/static/image/topic/default_topic_cover.svg";
            }
            it.setCover(cover);
        }
        return items;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addTopic(Topic topic, List<TopicItem> items) {
        int res = mapper.addTopic(topic);
        if (items != null && !items.isEmpty()) {
            for (TopicItem item : items) {
                item.setTopicId(topic.getId());
            }
            mapper.batchInsertItems(items);
        }
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTopic(Topic topic, List<TopicItem> items) {
        mapper.updateTopic(topic);
        if (items != null) {
            mapper.deleteTopicItems(topic.getId());
            if (!items.isEmpty()) {
                for (TopicItem item : items) {
                    item.setTopicId(topic.getId());
                }
                mapper.batchInsertItems(items);
            }
        }
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTopic(int id) {
        mapper.deleteTopicItems(id);
        return mapper.deleteTopic(id);
    }

    @Override
    public int changeTopicStatus(int id, boolean status) {
        return mapper.changeTopicStatus(id, status);
    }

    @Override
    public List<Topic> getPublicTopics(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return mapper.getPublicTopicList(offset, pageSize);
    }

    @Override
    public int getPublicTopicCount() {
        return mapper.getPublicTopicCount();
    }
} 