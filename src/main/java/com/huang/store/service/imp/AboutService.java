package com.huang.store.service.imp;

import com.huang.store.entity.notice.About;

public interface AboutService {
    int addAbout(About about);
    int modifyAbout(About about);
    About getAbout();
} 