package com.laith.evolution.repositories.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class BlacklistRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PREFIX = "blacklist:";

    public void addToBlacklist(String token, long durationInMinutes) {
        redisTemplate.opsForValue().set(PREFIX + token, "BLACKLISTED", durationInMinutes, TimeUnit.MINUTES);
    }

    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(PREFIX + token);
    }
}
