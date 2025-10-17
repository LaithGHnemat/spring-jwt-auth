package com.laith.evolution.repositories.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RateLimitRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PREFIX = "rate-limit:";

    public int increment(String key, long durationInSeconds) {
        Integer current = (Integer) redisTemplate.opsForValue().get(PREFIX + key);
        if (current == null) {
            redisTemplate.opsForValue().set(PREFIX + key, 1, durationInSeconds, TimeUnit.SECONDS);
            return 1;
        } else {
            redisTemplate.opsForValue().increment(PREFIX + key);
            return current + 1;
        }
    }

    public void reset(String key) {
        redisTemplate.delete(PREFIX + key);
    }
}
