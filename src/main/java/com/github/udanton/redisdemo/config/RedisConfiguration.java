package com.github.udanton.redisdemo.config;

import java.time.Duration;
import java.util.Set;

import io.lettuce.core.ReadFrom;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        RedisNode redisNode1 = RedisNode.newRedisNode()
            .listeningAt("redis-master.mylab.local", 26379)
            .build();
        RedisNode redisNode2 = RedisNode.newRedisNode()
            .listeningAt("redis-slave-1.mylab.local", 26380)
            .build();
//        RedisNode redisNode3 = RedisNode.newRedisNode()
//            .listeningAt("redis-slave-2.mylab.local", 26381)
//            .build();
        final RedisSentinelConfiguration sentinelConfiguration = new RedisSentinelConfiguration()
            .master("mymaster");
        sentinelConfiguration.addSentinel(redisNode1);
        sentinelConfiguration.addSentinel(redisNode2);
//        sentinelConfiguration.addSentinel(redisNode3);

        final LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
            .readFrom(ReadFrom.ANY_REPLICA)
            .build();

        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(sentinelConfiguration, lettuceClientConfiguration);
        connectionFactory.setDatabase(0);
        return connectionFactory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        return redisTemplate;
    }

    @Bean
    public CacheManager cacheManager(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
            .disableCachingNullValues();
        return RedisCacheManager.builder(lettuceConnectionFactory)
            .cacheDefaults(redisCacheConfiguration)
            .build();
    }

}
