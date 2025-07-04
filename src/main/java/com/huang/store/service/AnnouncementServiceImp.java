package com.huang.store.service;

import com.huang.store.entity.notice.Announcement;
import com.huang.store.mapper.AnnouncementMapper;
import com.huang.store.service.imp.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("announcementService")
public class AnnouncementServiceImp implements AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public int addAnnouncement(Announcement announcement) {
        return announcementMapper.addAnnouncement(announcement);
    }

    @Override
    public int deleteAnnouncement(int id) {
        return announcementMapper.deleteAnnouncement(id);
    }

    @Override
    public int modifyAnnouncement(Announcement announcement) {
        return announcementMapper.modifyAnnouncement(announcement);
    }

    @Override
    public Announcement getAnnouncement(int id) {
        return announcementMapper.getAnnouncement(id);
    }

    @Override
    public List<Announcement> getAnnouncementList(int page, int pageSize) {
        int start = (page - 1) * pageSize;
        return announcementMapper.getAnnouncementList(start, pageSize);
    }

    @Override
    public int getAnnouncementCount() {
        return announcementMapper.getAnnouncementCount();
    }

    @Override
    public List<Announcement> getEnabledAnnouncements() {
        return announcementMapper.getEnabledAnnouncements();
    }
} 