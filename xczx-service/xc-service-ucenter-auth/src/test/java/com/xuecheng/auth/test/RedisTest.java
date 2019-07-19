package com.xuecheng.auth.test;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Kiku
 * @date 2019/7/16 17:59
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void redisTest(){
        //key
        String key = "kiku";
        Map<String,String> map = new HashMap<>();
        map.put("name","hyx");
        map.put("adds","sss");
        String jsonString = JSON.toJSONString(map);
        stringRedisTemplate.boundValueOps(key).set(jsonString,30, TimeUnit.SECONDS);
        String value = stringRedisTemplate.opsForValue().get(key);
        System.out.println(value);
    }
}
