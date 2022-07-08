package com.shnu.seckill.config;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shnu.seckill.pojo.User;
import com.shnu.seckill.service.IUserService;
import com.shnu.seckill.utils.CookieUtil;
import com.shnu.seckill.utils.RespBean;
import com.shnu.seckill.utils.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * Author:RonClaus
 * Date:2022/7/8
 * Description:访问次数拦截器
 */
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {
    @Autowired
    private IUserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 访问次数拦截器
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod){
            User user = getUser(request, response);
            UserContext.setUser(user);
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit==null){
                return true;
            }
            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if (needLogin){
                if (user==null){
                    render(response, RespBeanEnum.SESSION_ERROR);
                    return false;
                }
                key += ":"+user.getId();
            }
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer count = (Integer) valueOperations.get(key);  //获取已访问次数
            if (count==null){
                valueOperations.set(key,1,second, TimeUnit.SECONDS);
            }else if (count<maxCount){
                valueOperations.increment(key);
            }else{
                render(response,RespBeanEnum.ACCESS_LIMIT);
                return false;
            }
        }
        return true;
    }

    /**
     *构建返回对象
     * @param response
     * @param sessionError
     */
    private void render(HttpServletResponse response, RespBeanEnum sessionError) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        RespBean respBean = RespBean.error(sessionError);
        out.write(new ObjectMapper().writeValueAsString(respBean));
        out.flush();
        out.close();
    }

    /**
     *获取当前登录用户
     * @param request
     * @param response
     * @return
     */
    private User getUser(HttpServletRequest request,HttpServletResponse response){
        String userTicket = CookieUtil.getCookieValue(request, "userTicket");
        if (StringUtils.isEmpty(userTicket)){
            return null;
        }
        return userService.getUserByReids(userTicket,request,response);
    }
}
