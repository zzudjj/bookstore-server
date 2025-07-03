package com.huang.store.mapper;

import com.huang.store.entity.topic.Topic;
import com.huang.store.entity.topic.TopicItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicV2Mapper {
    /* 列表分页 */
    List<Topic> getTopicList(@Param("offset") int offset, @Param("limit") int limit);
    int getTopicCount();

    /* 仅展示上线书单 */
    List<Topic> getPublicTopicList(@Param("offset") int offset,@Param("limit") int limit);
    int getPublicTopicCount();

    /* 详情 */
    Topic getTopic(@Param("id") int id);
    List<TopicItem> getTopicItems(@Param("topicId") int topicId);

    /* 管理端 */
    int addTopic(Topic topic);
    int updateTopic(Topic topic);
    int deleteTopic(@Param("id") int id);
    int changeTopicStatus(@Param("id") int id, @Param("status") boolean status);

    int batchInsertItems(@Param("items") List<TopicItem> items);
    int deleteTopicItems(@Param("topicId") int topicId);
} 