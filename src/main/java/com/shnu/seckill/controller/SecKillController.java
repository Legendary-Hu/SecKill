package com.shnu.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.shnu.seckill.info.GoodsInfo;
import com.shnu.seckill.pojo.Order;
import com.shnu.seckill.pojo.SeckillOrder;
import com.shnu.seckill.pojo.User;
import com.shnu.seckill.service.IGoodsService;
import com.shnu.seckill.service.IOrderService;
import com.shnu.seckill.service.ISeckillOrderService;
import com.shnu.seckill.utils.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Author:RonClaus
 * Date:2022/7/3
 * Description:None
 */
@Controller
@RequestMapping("/seckill")
public class SecKillController {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;


    /**
     * windows 优化前 QPS 2312
     * Linux  优化前 QPS  707
     * 秒杀
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("/doSecKill")
    public String doSecKIll(Model model, User user,Long goodsId){
        if(user==null){
            return "login";
        }
        model.addAttribute("user",user);
        GoodsInfo good = goodsService.findGoodById(goodsId);
        //判断库存
        if (good.getStockCount()<1){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMsg());
            return "secKillFail";
        }
        //判断是否重复抢购
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if (seckillOrder!=null){
            model.addAttribute("errmsg",RespBeanEnum.REPEATE_ERROR.getMsg());
            return "secKillFail";
        }
        Order order = orderService.seckill(user, good);
        model.addAttribute("order",order);
        model.addAttribute("goods",good);
        return "orderDetail";
    }
}
