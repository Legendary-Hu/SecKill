package com.shnu.seckill.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;


import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Author:RonClaus
 * Date:2022/7/1
 * Description:Redis 配置类实现序列化
 */
@Configuration
public class redisConfig {
    @Bean
    @SuppressWarnings("all")
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){ //Springboot2.x 自定义redisTemplate 需要添加高版本连接池依赖，会报找不不到redisConnectionFactory，其实已经存在容器中
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        //key序列化
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //value序列化
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        //hash key序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //hash value 序列化
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        //注入连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
}
