package com.laith.evolution.services;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
@Component
public class RedisChecker {
    private static final Logger logger = LoggerFactory.getLogger(RedisChecker.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void test() {
        try {
            redisTemplate.opsForValue().set("ping", "pong");
            String value = (String) redisTemplate.opsForValue().get("ping");
            logger.info("Redis test value: {}", value);
        } catch (Exception e) {
            logger.error("Redis connection failed", e);
        }
    }
}