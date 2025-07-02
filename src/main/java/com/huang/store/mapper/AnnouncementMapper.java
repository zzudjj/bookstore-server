package com.huang.store.mapper;

import com.huang.store.entity.notice.Announcement;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementMapper {

    int addAnnouncement(Announcement announcement);

    int deleteAnnouncement(int id);

    int modifyAnnouncement(Announcement announcement);

    Announcement getAnnouncement(int id);

    List<Announcement> getAnnouncementList(int page, int pageSize);

    int getAnnouncementCount();

    List<Announcement> getEnabledAnnouncements();
}