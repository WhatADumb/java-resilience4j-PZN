package self.study.resilience4j;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class RateLimiterRegistryTest {
    final private AtomicLong counter = new AtomicLong(0L);

    @Test
    void testConfig() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(250)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ofSeconds(3))
                .build();

        RateLimiterRegistry registry = RateLimiterRegistry.ofDefaults();
        registry.addConfiguration("config", config);

        RateLimiter limiter = registry.rateLimiter("limiter", "config");

        for(int i = 0; i < 1000; i++){
            Runnable runnable = RateLimiter.decorateRunnable(limiter, () -> {
                long incremented = counter.incrementAndGet();
                log.info("Result: {}", incremented);
            });

            runnable.run();
        }
    }
}
