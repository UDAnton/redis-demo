package com.github.udanton.redisdemo.cache;

import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheHelperValueOperations implements RedisCacheHelper {

    private static final String SEPARATOR_CACHE = ":";

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void put(String cacheName, Object key, Object value, long ttl) {
        redisTemplate.opsForValue().set(constructKey(cacheName, key), value, ttl, TimeUnit.MILLISECONDS);
    }

    @Override
    public Object get(String cacheName, Object key) {
        return redisTemplate.opsForValue().get(constructKey(cacheName, key));
    }

    private String constructKey(String cacheName, Object key) {
        return cacheName + SEPARATOR_CACHE + key;
    }

}
