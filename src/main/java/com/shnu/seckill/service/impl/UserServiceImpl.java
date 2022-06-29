package com.shnu.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shnu.seckill.info.LogInfo;
import com.shnu.seckill.mapper.UserMapper;
import com.shnu.seckill.pojo.User;
import com.shnu.seckill.service.IUserService;
import com.shnu.seckill.utils.MD5Util;
import com.shnu.seckill.utils.RespBean;
import com.shnu.seckill.utils.RespBeanEnum;
import com.shnu.seckill.utils.ValidatorUtil;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.validation.constraints.NotNull;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author huxiang
 * @since 2022-06-28
 */
@Service

public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public RespBean doLogin(LogInfo user) {

        String mobile = user.getMobile();
        String password = user.getPassword();
        //参数校验
//        if(StringUtils.isEmpty(mobile)||StringUtils.isEmpty(password)){
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//        }
//        if(!ValidatorUtil.isMobile(mobile)){
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }

        User user1 = userMapper.selectById(mobile);
        if (user1==null) return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        if(MD5Util.FromPassToDbPass(password,user1.getSalt()).equals(user1.getPassword())){
            return RespBean.success();
        }else{
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }

        
    }

}
