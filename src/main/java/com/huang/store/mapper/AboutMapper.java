package com.huang.store.mapper;

import com.huang.store.entity.notice.About;
import org.springframework.stereotype.Repository;

@Repository
public interface AboutMapper {

    int addAbout(About about);

    int modifyAbout(About about);

    About getAbout();
} 