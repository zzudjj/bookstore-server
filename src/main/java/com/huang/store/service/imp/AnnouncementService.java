package com.huang.store.service.imp;

import com.huang.store.entity.notice.Announcement;

import java.util.List;

public interface AnnouncementService {
    int addAnnouncement(Announcement announcement);
    int deleteAnnouncement(int id);
    int modifyAnnouncement(Announcement announcement);
    Announcement getAnnouncement(int id);
    List<Announcement> getAnnouncementList(int page, int pageSize);
    int getAnnouncementCount();
    List<Announcement> getEnabledAnnouncements();
} 