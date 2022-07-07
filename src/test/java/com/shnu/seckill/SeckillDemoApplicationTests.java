package com.shnu.seckill;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class SeckillDemoApplicationTests {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisScript<Boolean> redisScript;

    @Test
    public void contextLoads() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //占位，如果KEY不存在才可以设置成功
        Boolean isloock = valueOperations.setIfAbsent("k1", "v1");
        if (isloock){
            valueOperations.set("name","xxxx");
            String name = (String) valueOperations.get("name");
            System.out.println("name="+name);
            Integer.parseInt("xxx");
            //操作结束释放锁
            redisTemplate.delete("k1");
        }else{
            System.out.println("有线程在使用，请稍后再试");
        }
    }
    @Test
    public void testLock02(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //添加一个失效时间，防止应用再运行过程中锁无法释放
        Boolean look = valueOperations.setIfAbsent("k1", "v1", 5, TimeUnit.SECONDS);
        if (look){
            valueOperations.set("name","xxxx");
            String name = (String) valueOperations.get("name");
            System.out.println("name="+name );
//            Integer.parseInt("xxx");
            redisTemplate.delete("k1");
        }else{
            System.out.println("有线程在使用，请稍后");
        }
    }

    @Test
    public void testLock03(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String s = UUID.randomUUID().toString();
        Boolean lock = valueOperations.setIfAbsent("k1", s, 30, TimeUnit.SECONDS);
        if (lock){
            valueOperations.set("name","xxx");
            String name = (String) valueOperations.get("name");
            System.out.println("name="+name);
            System.out.println(valueOperations.get("k1"));
            Boolean res = (Boolean) redisTemplate.execute(redisScript, Collections.singletonList("k1"), s);
            System.out.println(res);
        }else{
            System.out.println("线程在使用，请稍后再试");
        }
    }
}
