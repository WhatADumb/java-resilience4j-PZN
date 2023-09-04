package self.study.resilience4j;

import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;

@Slf4j
public class DecorateTest {
    @SneakyThrows
    void slow(){
        log.info("slow");
        Thread.sleep(1_000L);
        throw new RuntimeException("500");
    }

    @Test
    void testDecorate() throws InterruptedException {
        RateLimiter limiter = RateLimiter.of("limiter", RateLimiterConfig.custom()
                .limitForPeriod(5)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .build());

        Retry retry = Retry.of("retry", RetryConfig.custom()
                .maxAttempts(10)
                .waitDuration(Duration.ofMillis(10))
                .build());

        Runnable runnable = Decorators.ofRunnable(() -> slow())
                .withRetry(retry).withRateLimiter(limiter).decorate();

        for(int i = 0; i < 100; i++){
            new Thread(() -> slow()).start();
        }

        Thread.sleep(10_000L);
    }
}
