package self.study.resilience4j;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class TimeLimiterTest {
    @SneakyThrows
    String slow(){
        log.info("Slow Start");
        Thread.sleep(5_000L);
        log.info("Slow Finish");
        return "sample";
    }

    @Test
    void testTimeLimiter() throws Exception {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<String> future = service.submit(() -> slow());

        TimeLimiter limiter = TimeLimiter.ofDefaults("sample");
        Callable<String> supplier = TimeLimiter.decorateFutureSupplier(limiter, () -> future);
        supplier.call();
    }

    @Test
    void testConfig() throws Exception {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<String> future = service.submit(() -> slow());

        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .cancelRunningFuture(true)
                .timeoutDuration(Duration.ofMinutes(1))
                .build();

        TimeLimiter limiter = TimeLimiter.of("sample", config);
        Callable<String> supplier = TimeLimiter.decorateFutureSupplier(limiter, () -> future);
        supplier.call();
    }

    @Test
    void testRegistry() throws Exception {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<String> future = service.submit(() -> slow());

        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .cancelRunningFuture(true)
                .timeoutDuration(Duration.ofMinutes(1))
                .build();

        TimeLimiterRegistry registry = TimeLimiterRegistry.ofDefaults();
        registry.addConfiguration("config", config);

        TimeLimiter limiter = registry.timeLimiter("sample", "config");
        Callable<String> supplier = TimeLimiter.decorateFutureSupplier(limiter, () -> future);
        supplier.call();
    }
}
