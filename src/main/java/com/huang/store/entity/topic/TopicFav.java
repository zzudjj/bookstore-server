package com.huang.store.entity.topic;

import java.sql.Timestamp;

public class TopicFav {
    private String userAccount;
    private int topicId;
    private Timestamp favAt;

    public String getUserAccount() { return userAccount; }
    public void setUserAccount(String userAccount) { this.userAccount = userAccount; }

    public int getTopicId() { return topicId; }
    public void setTopicId(int topicId) { this.topicId = topicId; }

    public Timestamp getFavAt() { return favAt; }
    public void setFavAt(Timestamp favAt) { this.favAt = favAt; }
} 