package com.joe.im.redis;

import com.joe.im.BaseTest;
import com.joe.im.common.bean.Demo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.Assert.*;

public class CacheOpTest extends BaseTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Test
    public void testKeys() throws Exception{

//        redisTemplate.opsForValue().set("hello", "world");
//        Object world = redisTemplate.opsForValue().get("hello");
//        assertTrue(world != null);
        //redisTemplate.opsForValue().set("demo", Demo.builder().id(1l).content("hello").build());
        Demo demo = (Demo) redisTemplate.opsForValue().get("demo");
        assertTrue(demo != null);
    }
}