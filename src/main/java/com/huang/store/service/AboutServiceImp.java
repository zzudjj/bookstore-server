package com.huang.store.service;

import com.huang.store.entity.notice.About;
import com.huang.store.mapper.AboutMapper;
import com.huang.store.service.imp.AboutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("aboutService")
public class AboutServiceImp implements AboutService {

    @Autowired
    private AboutMapper aboutMapper;

    @Override
    public int addAbout(About about) {
        return aboutMapper.addAbout(about);
    }

    @Override
    public int modifyAbout(About about) {
        return aboutMapper.modifyAbout(about);
    }

    @Override
    public About getAbout() {
        return aboutMapper.getAbout();
    }
} 