package com.shnu.seckill.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Author:RonClaus
 * Date:2022/6/28
 * Description:None
 */
@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {
    //通用
    SUCCESS(200,"SUCCESS"),
    ERROR(500,"服务端异常"),
    //登录模块
    MOBILE_ERROR(500230,"手机号码不正确！"),
    BINDING_ERROR(500211,"参数校验异常"),
    LOGIN_ERROR(500210,"用户名或密码错误");
    private final Integer code;
    private final String msg;

}
