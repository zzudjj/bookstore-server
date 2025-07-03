package com.huang.store.entity.topic;

import java.sql.Timestamp;
import java.util.List;

/**
 * 书单主题实体
 */
public class Topic {
    private int id;
    private String title;
    private String subTitle;
    private String cover;
    private int rank;
    private boolean status; // true=上架
    private int viewCnt;
    private int favCnt;
    private int orderCnt;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // 用于接收前端数据
    private List<TopicItem> items;

    // getter / setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubTitle() { return subTitle; }
    public void setSubTitle(String subTitle) { this.subTitle = subTitle; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public int getViewCnt() { return viewCnt; }
    public void setViewCnt(int viewCnt) { this.viewCnt = viewCnt; }

    public int getFavCnt() { return favCnt; }
    public void setFavCnt(int favCnt) { this.favCnt = favCnt; }

    public int getOrderCnt() { return orderCnt; }
    public void setOrderCnt(int orderCnt) { this.orderCnt = orderCnt; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public List<TopicItem> getItems() { return items; }
    public void setItems(List<TopicItem> items) { this.items = items; }

    @Override
    public String toString() {
        return "Topic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", cover='" + cover + '\'' +
                ", rank=" + rank +
                ", status=" + status +
                ", viewCnt=" + viewCnt +
                ", favCnt=" + favCnt +
                ", orderCnt=" + orderCnt +
                '}';
    }
} 