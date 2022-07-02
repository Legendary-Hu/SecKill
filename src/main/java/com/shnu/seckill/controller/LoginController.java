package com.shnu.seckill.controller;

import com.shnu.seckill.info.LogInfo;
import com.shnu.seckill.pojo.User;
import com.shnu.seckill.service.IUserService;
import com.shnu.seckill.service.impl.UserServiceImpl;
import com.shnu.seckill.utils.MD5Util;
import com.shnu.seckill.utils.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author:RonClaus
 * Date:2022/6/28
 * Description:None
 */
@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {
    @Autowired
    private IUserService userService;

    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    @RequestMapping(value = "/doLogin",method = RequestMethod.POST)
    @ResponseBody
    public RespBean dologin(@Validated LogInfo user, HttpServletRequest request, HttpServletResponse response){
//        log.info(user.getMobile()+user.getPassword());
        return userService.doLogin(user,request,response);


    }
}
