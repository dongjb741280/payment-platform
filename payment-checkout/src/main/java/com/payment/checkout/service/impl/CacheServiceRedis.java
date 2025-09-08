package com.payment.checkout.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.checkout.service.CacheService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class CacheServiceRedis implements CacheService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> void set(String key, T value, long seconds) {
        try {
            String json = objectMapper.writeValueAsString(value);
            stringRedisTemplate.opsForValue().set(key, json, seconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            // ignore cache errors
        }
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (json == null) return null;
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void delete(String key) {
        try {
            stringRedisTemplate.delete(key);
        } catch (Exception e) {
            // ignore
        }
    }
}


