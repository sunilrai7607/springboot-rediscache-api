package com.sbr.rest.api.redis.redisapi;

import com.sbr.rest.api.redis.redisapi.service.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = RedisService.class)
@ActiveProfiles("test")
class RedisApiApplicationTests {

    @Test
    void contextLoads() {
    }

}
