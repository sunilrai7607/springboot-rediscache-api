package com.sbr.rest.api.redis.redisapi.service.impl;

import com.sbr.rest.api.redis.redisapi.model.UserProfile;
import com.sbr.rest.api.redis.redisapi.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate redisTemplate;
    private final RedisCacheManager redisCacheManager;

    private Map<String, Object> tempStore = new ConcurrentHashMap<>();

    @Autowired
    public RedisServiceImpl(RedisTemplate redisTemplate, RedisCacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
        populate();
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Cacheable(value = "UserProfile", key = "#id")
    public UserProfile getById(String id, String sessionId) {
        log.info("Redis template : {} ", redisTemplate);
        this.redisCacheManager.getCache("user-session").put(sessionId, tempStore.get(id));
        return (UserProfile) tempStore.get(id);
    }

    private void populate() {
        this.tempStore.put("1", new UserProfile("1", "Sheila", "Lowe", "Sheila.Lowe@gmail.com"));
        this.tempStore.put("2", new UserProfile("2", "Wilma", "Thomas", "Thomas@gmail.com"));
    }
}
