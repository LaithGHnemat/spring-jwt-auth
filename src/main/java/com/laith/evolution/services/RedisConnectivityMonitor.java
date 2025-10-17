package com.laith.evolution.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


@Log4j2
@Component
@RequiredArgsConstructor
public class RedisConnectivityMonitor {

    private final RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void testConnection() {
        try {
            redisTemplate.opsForValue().set("ping", "pong");
            String value = (String) redisTemplate.opsForValue().get("ping");
            log.info("Redis test value: " , value);
        } catch (Exception e) {
            log.error("Redis connection failed", e.getMessage());
        }
    }
}