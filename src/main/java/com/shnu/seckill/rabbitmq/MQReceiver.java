package com.shnu.seckill.rabbitmq;

import com.shnu.seckill.info.GoodsInfo;
import com.shnu.seckill.info.SeckillMessage;
import com.shnu.seckill.pojo.Order;
import com.shnu.seckill.pojo.SeckillOrder;
import com.shnu.seckill.pojo.User;
import com.shnu.seckill.service.IGoodsService;
import com.shnu.seckill.service.IOrderService;
import com.shnu.seckill.utils.JSONUtil;
import com.shnu.seckill.utils.RespBean;
import com.shnu.seckill.utils.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Author:RonClaus
 * Date:2022/7/6
 * Description:None
 */
@Service
@Slf4j
public class MQReceiver {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IOrderService orderService;

//    @RabbitListener(queues = "queue")
//    public void receiver(Object msg){
//        log.info("接收消息："+msg);
//
//    }
//    @RabbitListener(queues = "queue_fanout01")
//    public void receiver01(Object msg){
//        log.info("QUEUE01接收消息："+msg);
//
//    }
//    @RabbitListener(queues = "queue_fanout02")
//    public void receiver02(Object msg){
//        log.info("QUEUE02接收消息："+msg);
//
//    }
//    @RabbitListener(queues = "queue_direct01")
//    public void receiver03(Object msg){
//        log.info("QUEUE01接收消息："+msg);
//
//    }
//    @RabbitListener(queues = "queue_direct02")
//    public void receiver04(Object msg){
//        log.info("QUEUE02接收消息："+msg);
//    }
//    @RabbitListener(queues = "queue_topic01")
//    public void receiver05(Object msg){
//        log.info("QUEUE01接收消息："+msg);
//    }
//    @RabbitListener(queues = "queue_topic02")
//    public void receiver06(Object msg){
//        log.info("QUEUE02接收消息："+msg);
//    }
//    @RabbitListener(queues = "queue_header01")
//    public void receiver07(Message msg){
//        log.info("QUEUE01接收消息："+new String(msg.getBody()));
//    }
//    @RabbitListener(queues = "queue_header02")
//    public void receiver08(Message msg){
//        log.info("QUEUE02接收消息："+new String(msg.getBody()));
//    }

    /**
     * 下单操作
     * @param msg
     */

    @RabbitListener(queues = "seckillQueue")
    public void receiverSeckillMessage(String msg){
        log.info("接收秒杀信息："+msg);
        SeckillMessage seckillMessage = JSONUtil.jsonStr2Object(msg, SeckillMessage.class);
        Long goodId = seckillMessage.getGoodId();
        User user = seckillMessage.getUser();
        GoodsInfo good = goodsService.findGoodById(goodId);
        //判断库存
        if (good.getStockCount()<1){
            return;
        }
        //判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + good.getId());
        if (seckillOrder!=null){
            return;
        }
        //下单操作
        orderService.seckill(user, good);

    }
}
