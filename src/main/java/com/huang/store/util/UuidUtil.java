package com.huang.store.util;

import java.util.UUID;

/**
 * @description: UUID工具类
 */
public final class UuidUtil {
    private UuidUtil(){}
    public static String getUuid(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
