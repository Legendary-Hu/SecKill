package com.shnu.seckill.controller;


import com.shnu.seckill.info.GoodsInfo;
import com.shnu.seckill.pojo.User;
import com.shnu.seckill.service.IGoodsService;
import com.shnu.seckill.service.IUserService;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Resource
    private ThymeleafViewResolver thymeleafViewResolver;

    /**
     * windows 优化前QPS 3489
     * Linux 优化前QPS   846
     * 跳转商品页面
     * @param user
     * @param model
     * @return
     */
    @RequestMapping(value = "/toList",produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(User user, Model model,HttpServletRequest request, HttpServletResponse response){
//        if(StringUtils.isEmpty(ticket)){
//            return "login";
//        }
////        User user = (User) session.getAttribute(ticket);
//        User user = userService.getUserByReids(ticket, request, response);
//        if(user==null){
//            return "login";
//        }
        //因为采用thymeleaf,采用页面缓存，从redis中获取页面,redis中页面不为空
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if (!StringUtils.isEmpty(html)){
            return html;
        }
        model.addAttribute("user",user);
        model.addAttribute("goodsList",goodsService.getGoods());
        //如果页面为空,手动渲染页面并存入redis
        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", context); //goodsList为templates中的html
        if (!StringUtils.isEmpty(html)){
            valueOperations.set("goodsList",html,1, TimeUnit.MINUTES);
        }
        return html;
    }

    /**
     * 跳转商品详情页
     * url缓存
     * @param user
     * @param model
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/toDetail/{goodsId}",produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail(User user, Model model, @PathVariable Long goodsId,HttpServletRequest request,HttpServletResponse response){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodDetail:"+goodsId);
        if (!StringUtils.isEmpty(html)){
            return html;
        }
        model.addAttribute("user",user);
        GoodsInfo good = goodsService.findGoodById(goodsId);
        Date startDate = good.getStartDate();
        Date endDate = good.getEndDate();
        Date nowDate = new Date();
        //秒杀状态
        int secKillStatus = 0;
        //秒杀倒计时
        int remainSeconds = 0;
        if (nowDate.before(startDate)){
            remainSeconds = (int) ((startDate.getTime()-nowDate.getTime())/1000);
        }else if (nowDate.after(endDate)){
            secKillStatus = 2;
            remainSeconds = -1;
        }else{
            secKillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("remainSeconds",remainSeconds);
        model.addAttribute("secKillStatus",secKillStatus);
        model.addAttribute("goods",good);
        //手动渲染页面，并存入redis
        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodDetail", context);
        if (!StringUtils.isEmpty(html)){
            valueOperations.set("goodDetail:"+goodsId,html,60,TimeUnit.SECONDS); //将页面存入缓存
        }
        return html;
    }
}
