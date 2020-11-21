package com.sbr.rest.api.redis.redisapi.controller;

import com.sbr.rest.api.redis.redisapi.model.UserProfile;
import com.sbr.rest.api.redis.redisapi.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "api/v1/redis-api", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class RedisController {

    private final RedisService redisService;

    @Autowired
    public RedisController(RedisService redisService) {
        this.redisService = redisService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfile> getId(@PathVariable("id") String id, HttpServletRequest request, HttpSession session) {
        log.info("HttpServletRequest : {} ", request);
        log.info("HttpSession : {} ", session.getId());
        return ResponseEntity.ok(redisService.getById(id, session.getId()));
    }
}
