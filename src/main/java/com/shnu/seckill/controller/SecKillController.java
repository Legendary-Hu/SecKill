package com.shnu.seckill.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.shnu.seckill.config.AccessLimit;
import com.shnu.seckill.exception.GlobalException;
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
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private RedisScript<Long> redisScript;

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
     * @param path
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/{path}/doSecKill",method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKIll(@PathVariable String path, User user, Long goodsId){
        if(user==null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        boolean check = orderService.checkPath(user,goodsId,path);
        if (!check){
           return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //通过内存标记减少Redis的访问
        if (EmptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder!=null){
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }

        //预减库存
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
//        Long stock = (Long) redisTemplate.execute(redisScript, Collections.singletonList("seckillGoods:" + goodsId));
        if (stock<0){
            EmptyStockMap.put(goodsId,true);
//            valueOperations.set("isStockEmpty:"+goodsId,"0");
            valueOperations.increment("seckillGoods:"+goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JSONUtil.object2JsonStr(seckillMessage));


        return RespBean.success(0);


    }

    /**
     * 获取秒杀地址
     * @param user
     * @param goodsId
     * @param captcha
     * @param request
     * @return
     */
    @AccessLimit(second=5,maxCount=5,needLogin=true)
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request){
        if (user==null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        boolean check = orderService.checkCaptcha(user,goodsId,captcha);
        if (!check){
            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
        }
        String str = orderService.createPath(user,goodsId);
        return RespBean.success(str);
    }

    /**
     * 验证码接口
     * @param user
     * @param goodsId
     * @param response
     */
    @RequestMapping(value = "captcha",method = RequestMethod.GET)
    public void verifyCode(User user, Long goodsId, HttpServletResponse response){
        if (user==null||goodsId<0){
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        response.setContentType("image/jpg");
        response.setHeader("Pargam","No-cache");
        response.setHeader("Cache-Control","no-cache");
        response.setDateHeader("Expires",0);
        //生成验证码,将结果放入redis
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:"+user.getId()+":"+goodsId,captcha.text(),300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败",e.getMessage());
        }
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
