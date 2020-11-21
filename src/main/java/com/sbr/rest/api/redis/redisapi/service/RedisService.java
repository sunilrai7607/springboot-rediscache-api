package com.sbr.rest.api.redis.redisapi.service;

import com.sbr.rest.api.redis.redisapi.model.UserProfile;

public interface RedisService {

    UserProfile getById(final String id, String sessionId);
}
