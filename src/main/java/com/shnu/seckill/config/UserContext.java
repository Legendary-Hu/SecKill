package com.shnu.seckill.config;

import com.shnu.seckill.pojo.User;

/**
 * Author:RonClaus
 * Date:2022/7/8
 * Description:None
 */
public class UserContext {
    private static ThreadLocal<User> userHolder = new ThreadLocal<>();
    public static void setUser( User user){
        userHolder.set(user);
    }
    public static User getUser(){
        return  userHolder.get();
    }
}
