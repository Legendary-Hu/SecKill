package com.shnu.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shnu.seckill.exception.GlobalException;
import com.shnu.seckill.info.LogInfo;
import com.shnu.seckill.mapper.UserMapper;
import com.shnu.seckill.pojo.User;
import com.shnu.seckill.service.IUserService;
import com.shnu.seckill.utils.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 登录功能实现
     * @param user
     * @param request
     * @param response
     * @return
     */
    @Override
    public RespBean doLogin(LogInfo user, HttpServletRequest request, HttpServletResponse response) {

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
        if (user1==null) throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        //判断密码是否正确
        if(!MD5Util.FromPassToDbPass(password,user1.getSalt()).equals(user1.getPassword())){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);

        }
        String ticke = UUIDUtil.uuid(); //生成UUID
//        request.getSession().setAttribute(ticke,user1);
        redisTemplate.opsForValue().set("user:"+ticke,user1); //往redis存用户信息实现分布式session
        CookieUtil.setCookie(request,response,"userTicket",ticke);


        return RespBean.success(ticke);
    }

    /**
     * 从redis获取用户信息
     * @param ticket
     * @param request
     * @param response
     * @return
     */
    @Override
    public User getUserByReids(String ticket, HttpServletRequest request, HttpServletResponse response) {
        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
        if (user!=null){
            //刷新Cookie信息
            CookieUtil.setCookie(request,response,"userTicket",ticket);
        }

        return user;
    }

}
