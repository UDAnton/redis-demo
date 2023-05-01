package com.github.udanton.redisdemo.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    private final static String HIT_DB_COUNT = "users_db_hit_count";
    private final static String HIT_CACHE_COUNT = "users_cache_hit_count";
    private final static String MISS_CACHE_COUNT = "users_cache_miss_count";
    private final static String RECOMPUTED_CACHE_COUNT = "recomputed_cache_count";

    @Bean
    public MeterRegistry getMeterRegistry() {
        return new CompositeMeterRegistry();
    }

    @Bean(name = "hitCacheMetricsCounter")
    public Counter getHitCacheMetricsCounter(MeterRegistry meterRegistry) {
        return meterRegistry.counter(HIT_CACHE_COUNT);
    }

    @Bean(name = "missCacheMetricsCounter")
    public Counter getMissCacheMetricsCounter(MeterRegistry meterRegistry) {
        return meterRegistry.counter(MISS_CACHE_COUNT);
    }

    @Bean(name = "hitDBMetricsCounter")
    public Counter getHitDBMetricsCounter(MeterRegistry meterRegistry) {
        return meterRegistry.counter(HIT_DB_COUNT);
    }

    @Bean(name = "recomputedMetricsCounter")
    public Counter getRecomputedMetricsCounter(MeterRegistry meterRegistry) {
        return meterRegistry.counter(RECOMPUTED_CACHE_COUNT);
    }

}
