package com.joe.im.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class CacheOp {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public Set<String> keys(String pattern){
        return redisTemplate.keys(pattern);
    }

    public boolean expire(String key, long time) {
        return redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    public long getExpire(String key){
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    public boolean hasKey(String key){
        return redisTemplate.hasKey(key);
    }

    public void del(String... key){
        if (key!=null&&key.length>0){
            if (key.length==1){
                redisTemplate.delete(key[0]);
            }else {
                redisTemplate.delete(Arrays.asList(key));
            }
        }
    }

    


}
