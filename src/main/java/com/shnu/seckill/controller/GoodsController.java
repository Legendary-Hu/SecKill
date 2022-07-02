package com.shnu.seckill.controller;


import com.shnu.seckill.pojo.User;
import com.shnu.seckill.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Author:RonClaus
 * Date:2022/6/30
 * Description:None
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private IUserService userService;

    /**
     * 跳转商品页面
     * @param user
     * @param model
     * @return
     */
    @RequestMapping("/toList")
    public String toList(User user, Model model){
//        if(StringUtils.isEmpty(ticket)){
//            return "login";
//        }
////        User user = (User) session.getAttribute(ticket);
//        User user = userService.getUserByReids(ticket, request, response);
//        if(user==null){
//            return "login";
//        }
        model.addAttribute("user",user);
        return "goods";
    }
}
