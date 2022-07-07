package com.shnu.seckill.controller;

import com.alibaba.fastjson.support.hsf.HSFJSONUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.shnu.seckill.info.GoodsInfo;
import com.shnu.seckill.info.SeckillMessage;
import com.shnu.seckill.pojo.Order;
import com.shnu.seckill.pojo.SeckillOrder;
import com.shnu.seckill.pojo.User;
import com.shnu.seckill.rabbitmq.MQSender;
import com.shnu.seckill.service.IGoodsService;
import com.shnu.seckill.service.IOrderService;
import com.shnu.seckill.service.ISeckillOrderService;
import com.shnu.seckill.utils.JSONUtil;
import com.shnu.seckill.utils.RespBean;
import com.shnu.seckill.utils.RespBeanEnum;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author:RonClaus
 * Date:2022/7/3
 * Description:None
 */
@Controller
@RequestMapping("/seckill")
@Slf4j
public class SecKillController implements InitializingBean {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;

    private Map<Long, Boolean> EmptyStockMap = new HashMap<>();


    /**
     * windows 优化前 QPS 2312
     * Linux  优化前 QPS  707
     * 秒杀
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping( "/doSecKill2")
    public String doSecKIll2(Model model, User user,Long goodsId){
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

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return orderId 成功，-1 秒杀失败，0排队中
     */
    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user,Long goodsId){
        if (user==null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user,goodsId);
        return RespBean.success(orderId);
    }




    /**
     * windows 优化前 QPS 2312
     *         优化后 QPS 2584
     *页面静态化+秒杀订单缓存 解决库存超卖+redis预减库存+mq异步下单+内存标记
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/doSecKill",method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKIll(Model model, User user, Long goodsId){
        if(user==null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        //通过内存标记减少Redis的访问
        if (EmptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder!=null){
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }

        //预减库存
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        if (stock<0){
            EmptyStockMap.put(goodsId,true);
//            valueOperations.set("isStockEmpty:"+goodsId,"0");
            valueOperations.increment("seckillGoods:"+goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JSONUtil.object2JsonStr(seckillMessage));


        return RespBean.success(0);



//        GoodsInfo good = goodsService.findGoodById(goodsId);
//        //判断库存
//        if (good.getStockCount()<1){
//            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMsg());
//            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
//        }
//        //判断是否重复抢购
////        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
//        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + good.getId());
//        if (seckillOrder!=null){
//            model.addAttribute("errmsg",RespBeanEnum.REPEATE_ERROR.getMsg());
//            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
//        }
//        Order order = orderService.seckill(user, good);
//
//        return RespBean.success(order);
    }

    /**
     * 初始化时执行的方法，把商品库存数量加载到redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsInfo> list = goodsService.getGoods();
        if (CollectionUtils.isEmpty(list)){
            return;
        }
        list.forEach(goodsInfo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsInfo.getId(), goodsInfo.getStockCount());
            EmptyStockMap.put(goodsInfo.getId(),false);
//            log.info("good:"+goodsInfo.getId()+"stock:"+goodsInfo.getStockCount());
        });
    }
}
