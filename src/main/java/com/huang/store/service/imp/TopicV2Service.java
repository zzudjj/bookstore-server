package com.huang.store.service.imp;

import com.huang.store.entity.topic.Topic;
import com.huang.store.entity.topic.TopicItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("topicV2Service")
public interface TopicV2Service {
    List<Topic> getTopicList(int page, int pageSize);
    int getTopicCount();

    Topic getTopic(int id);
    List<TopicItem> getTopicItems(int topicId);

    /* 管理端 */
    int addTopic(Topic topic, List<TopicItem> items);
    int updateTopic(Topic topic, List<TopicItem> items);
    int deleteTopic(int id);
    int changeTopicStatus(int id, boolean status);

    List<Topic> getPublicTopics(int page,int pageSize);
    int getPublicTopicCount();
} 