package com.shnu.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shnu.seckill.info.LogInfo;
import com.shnu.seckill.pojo.User;
import com.shnu.seckill.utils.RespBean;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author huxiang
 * @since 2022-06-28
 */
public interface IUserService extends IService<User> {

    RespBean doLogin(LogInfo user);
}
