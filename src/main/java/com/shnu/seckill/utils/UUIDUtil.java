package com.shnu.seckill.utils;

import java.util.UUID;

/**
 * Author:RonClaus
 * Date:2022/6/30
 * Description:UUID工具类
 */
public class UUIDUtil {
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
