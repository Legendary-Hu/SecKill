package com.shnu.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Author:RonClaus
 * Date:2022/7/6
 * Description:None
 */
@Service
@Slf4j
public class MQSender {
    @Resource
    private RabbitTemplate rabbitTemplate;
    //fanout模式
//    public void send(Object msg){
//        log.info("发送消息："+msg);
//        rabbitTemplate.convertAndSend("fanoutExchange","",msg);
//
//    }
//    //direct模式
//    public void send01(Object msg){
//        log.info("发送消息：" + msg);
//        rabbitTemplate.convertAndSend("directExchange","queue.red",msg);
//    }
//    public void send02(Object msg){
//        log.info("发送消息：" + msg);
//        rabbitTemplate.convertAndSend("directExchange","queue.green",msg);
//    }
//    //Topic模式
//    public void send03(Object msg){
//        log.info("发送消息："+msg);
//        rabbitTemplate.convertAndSend("topicExchange","red.rabbit.queue.rabbit",msg);
//    }
//    public void send04(Object msg){
//        log.info("发送消息："+msg);
//        rabbitTemplate.convertAndSend("topicExchange","rabbit.queue.rabbit",msg);
//    }
//    //Headers模式
//    public void send05(String msg){
//        log.info("发送消息："+msg);
//        MessageProperties properties = new MessageProperties();
//        properties.setHeader("color","red");
//        properties.setHeader("speed","slow");
//        Message message = new Message(msg.getBytes(), properties);
//        rabbitTemplate.convertAndSend("headerExchange","",message);
//    }
//    public void send06(String msg){
//        log.info("发送消息："+msg);
//        MessageProperties properties = new MessageProperties();
//        properties.setHeader("color","red");
//        properties.setHeader("speed","fast");
//        Message message = new Message(msg.getBytes(), properties);
//        rabbitTemplate.convertAndSend("headerExchange","",message);
//    }
    //发送秒杀信息
    public void sendSeckillMessage(String msg){
        log.info("发送秒杀信息："+msg);
        rabbitTemplate.convertAndSend("seckillExchange","seckill.message",msg);
    }
}
