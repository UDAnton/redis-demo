package com.github.udanton.redisdemo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import com.github.udanton.redisdemo.cache.CacheUser;
import com.github.udanton.redisdemo.cache.RedisCacheHelperValueOperations;
import com.github.udanton.redisdemo.persistence.User;
import com.github.udanton.redisdemo.persistence.UserRepository;
import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final static String CACHE_NAME = "users";
    private static final Duration DEFAULT_TTL = Duration.of(5, ChronoUnit.MINUTES);

    private final Counter hitCacheMetricsCounter;
    private final Counter missCacheMetricsCounter;
    private final Counter hitDBMetricsCounter;
    private final Counter recomputedMetricsCounter;
    private final UserRepository userRepository;
    private final RedisCacheHelperValueOperations redisCacheHelperValueOperations;

    public User createRandomUser() {
        User user = new User();
        user.setName(randomName());
        user.setEmail(String.format("%s.gmail.com", user.getName()));
        user.setBirthYear(randomDate());
        return userRepository.save(user);
    }

    public User getUserByProbabilisticApproach(Long id) {
        CacheUser cacheUser = (CacheUser) redisCacheHelperValueOperations.get(CACHE_NAME, id);
        if (cacheUser == null) {
            missCacheMetricsCounter.increment();
            return putUserToCache(id);
        } else {
            hitCacheMetricsCounter.increment();
            if (System.currentTimeMillis() - cacheUser.getDelta() * 200 * Math.log(Math.random())  >= cacheUser.getExpiration()) {
                putUserToCache(id);
                recomputedMetricsCounter.increment();
            }
            return cacheUser.getUser();
        }
    }

    private User putUserToCache(Long id) {
        long start = System.currentTimeMillis();
        User user = userRepository.findById(id).orElseThrow();
        long delta = System.currentTimeMillis() - start;
        CacheUser newCacheUser = createCacheUser(System.currentTimeMillis() + DEFAULT_TTL.toMillis(), delta, user);
        redisCacheHelperValueOperations.put(CACHE_NAME, id, newCacheUser, DEFAULT_TTL.toMillis());
        hitDBMetricsCounter.increment();
        return newCacheUser.getUser();
    }

    public User findUserById(Long id) {
        User user = (User) redisCacheHelperValueOperations.get(CACHE_NAME, id);
        if (user == null) {
            user = userRepository.findById(id).orElseThrow();
            redisCacheHelperValueOperations.put(CACHE_NAME, id, user, DEFAULT_TTL.toMillis());
            missCacheMetricsCounter.increment();
            hitDBMetricsCounter.increment();
            log.info("User with id={} was retrieved from DB", id);
        } else {
            hitCacheMetricsCounter.increment();
            log.info("User with id={} was retrieved from cache", id);
        }
        return user;
    }

    private static CacheUser createCacheUser(Long expiration, Long delta, User user) {
        CacheUser cacheUser = new CacheUser();
        cacheUser.setUser(user);
        cacheUser.setDelta(delta);
        cacheUser.setExpiration(expiration);
        return cacheUser;
    }

    private static String randomName() {
        return RandomStringUtils.random(10, true, false);
    }

    private static Date randomDate() {
        return new Date(ThreadLocalRandom.current().nextInt() * 1000L);
    }

}
