package self.study.resilience4j;

import io.github.resilience4j.ratelimiter.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class RateLimiterTest {
    final private AtomicLong counter = new AtomicLong(0L);

    @Test
    void testRateLimiter() {
        RateLimiter limiter = RateLimiter.ofDefaults("limit");

        for (int i = 0; i < 1_000; i++) {
            Runnable runnable = RateLimiter.decorateRunnable(limiter, () -> {
                long incremented = counter.incrementAndGet();
                log.info("Result: {}", incremented);
            });

            runnable.run();
        }
    }
}
