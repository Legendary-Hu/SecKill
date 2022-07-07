package com.shnu.seckill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.Topic;

/**
 * Author:RonClaus
 * Date:2022/7/6
 * Description:None
 */
@Configuration
public class RabbitMQTopicConfig {
//    private static final String QUEUE01 = "queue_topic01";
//    private static final String QUEUE02 = "queue_topic02";
//    private static final String EXCHANGE = "topicExchange";
//    private static final String ROUTINGKEY01 = "#.queue.#";
//    private static final String ROUTINGKEY02 = "*.queue.#";
//
//    @Bean
//    public Queue queue01(){
//        return new Queue(QUEUE01);
//    }
//    @Bean
//    public Queue queue02(){
//        return new Queue(QUEUE02);
//    }
//    @Bean
//    public TopicExchange topicExchage(){
//        return new TopicExchange(EXCHANGE);
//    }
//    @Bean
//    public Binding binding01(){
//        return BindingBuilder.bind(queue01()).to(topicExchage()).with(ROUTINGKEY01);
//    }
//    @Bean
//    public Binding binding02(){
//        return BindingBuilder.bind(queue02()).to(topicExchage()).with(ROUTINGKEY02);
//    }
    private static final String QUEUE = "seckillQueue";
    private static final String EXCHANGE = "seckillExchange";

    @Bean
    public Queue queue(){
        return new Queue(QUEUE);
    }
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(EXCHANGE);
    }
    @Bean
    public Binding binding(){
        return BindingBuilder.bind(queue()).to(topicExchange()).with("seckill.#");
    }
}
