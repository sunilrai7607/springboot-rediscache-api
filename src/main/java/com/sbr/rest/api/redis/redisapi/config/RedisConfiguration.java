package com.sbr.rest.api.redis.redisapi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@ConditionalOnExpression(
        "${redis.enabled:true}"
)
@Slf4j
@EnableCaching
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = RedisConfiguration.HTTPSESSION_MAX_INTERVAL)
public class RedisConfiguration {

    public static final int HTTPSESSION_MAX_INTERVAL = 900;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName("redis-server");
        factory.setPort(6379);
        factory.setUsePool(true);
        factory.getPoolConfig().setMaxIdle(30);
        factory.getPoolConfig().setMinIdle(10);
        return factory;
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setValueSerializer(new GenericToStringSerializer(Object.class));
        redisTemplate.setExposeConnection(true);
        return redisTemplate;
    }

    @Bean
    public RedisCacheManager redisCacheManager() {
        RedisCacheManager redisCacheManager = RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(jedisConnectionFactory())
                .cacheDefaults(redisCacheConfig())
                .build();
        redisCacheManager.setTransactionAware(true);
        return redisCacheManager;
    }

    private RedisCacheConfiguration redisCacheConfig() {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
                .disableCachingNullValues();
        //.entryTtl(Duration.ofSeconds(10));
        redisCacheConfiguration.usePrefix();
        return redisCacheConfiguration;
    }
}
