package com.huang.store.entity.topic;

/**
 * 书单条目
 */
public class TopicItem {
    private int topicId;
    private int bookId;
    private String recommendReason;
    private int orderNo;
    private String cover;

    public int getTopicId() { return topicId; }
    public void setTopicId(int topicId) { this.topicId = topicId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public String getRecommendReason() { return recommendReason; }
    public void setRecommendReason(String recommendReason) { this.recommendReason = recommendReason; }

    public int getOrderNo() { return orderNo; }
    public void setOrderNo(int orderNo) { this.orderNo = orderNo; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }
} 