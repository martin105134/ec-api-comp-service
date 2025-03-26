package com.example.ecapicompservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CompService {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public String getRedisValue(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void setValue(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }


}
