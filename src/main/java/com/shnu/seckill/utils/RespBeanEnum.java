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
    //登录模块5002
    MOBILE_ERROR(500230,"手机号码不正确！"),
    BINDING_ERROR(500211,"参数校验异常"),
    LOGIN_ERROR(500210,"用户名或密码错误"),
    MOBILE_NOT_EXIST(500213,"手机号码不存在"),
    PASSWD_UPDATE_FAIL(500214,"密码修改失败"),
    SESSION_ERROR(500215,"用户不存在"),
    //秒杀模块5005xx
    EMPTY_STOCK(500500,"库存不充足"),
    REPEATE_ERROR(500501,"该商品每人限购一件"),
    //订单模块
    ORDER_NOT_EXISI(500300,"订单不存在")
    ;
    private final Integer code;
    private final String msg;

}
