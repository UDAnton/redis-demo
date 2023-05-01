package com.github.udanton.redisdemo.cache;

public interface RedisCacheHelper {

    void put(String cacheName, Object key, Object value, long ttl);

    Object get(String cacheName, Object key);

}
