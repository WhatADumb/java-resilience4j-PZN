package self.study.resilience4j;

import io.github.resilience4j.bulkhead.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@Slf4j
public class BulkheadConfigTest {

    private AtomicLong counter = new AtomicLong(0L);

    @SneakyThrows
    void slow(){
        long data = counter.incrementAndGet();
        log.info("slow: {}", data);
        Thread.sleep(1_000L);
    }

    @Test
    void testSemaphore() throws InterruptedException {
        Bulkhead bulkhead = Bulkhead.ofDefaults("sema");

        for (int i = 0; i < 1000; i++) {
            Runnable runnable = Bulkhead.decorateRunnable(bulkhead, () -> slow());
            new Thread(runnable).start();
        }

        Thread.sleep(10_000L);
    }

    @Test
    void testThreadPool() {
        ThreadPoolBulkhead bulkhead = ThreadPoolBulkhead.ofDefaults("pool");

        for(int i = 0; i < 1000; i++){
            Supplier<CompletionStage<Void>> supplier = ThreadPoolBulkhead.decorateRunnable(bulkhead, () -> slow());
            supplier.get();
        }
    }

    @Test
    void testSemaphoreConfig() throws InterruptedException {
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(5)
                .maxWaitDuration(Duration.ofSeconds(5))
                .build();

        Bulkhead bulkhead = Bulkhead.of("sema", config);

        for (int i = 0; i < 1000; i++) {
            Runnable runnable = Bulkhead.decorateRunnable(bulkhead, () -> slow());
            new Thread(runnable).start();
        }

        Thread.sleep(10_000L);
    }

    @Test
    void testThreadPoolConfig() throws InterruptedException {
        ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
                .coreThreadPoolSize(5)
                .maxThreadPoolSize(5)
                .build();

        ThreadPoolBulkhead bulkhead = ThreadPoolBulkhead.of("pool", config);

        for(int i = 0; i < 25; i++){
            Supplier<CompletionStage<Void>> supplier = ThreadPoolBulkhead.decorateRunnable(bulkhead, () -> slow());
            supplier.get();
        }

        Thread.sleep(10_000L);
    }

    @Test
    void testSemaphoreRegistry() throws InterruptedException {
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(5)
                .maxWaitDuration(Duration.ofSeconds(5))
                .build();

        BulkheadRegistry registry = BulkheadRegistry.ofDefaults();
        registry.addConfiguration("config", config);

        Bulkhead bulkhead = registry.bulkhead("sema", "config");

        for (int i = 0; i < 1000; i++) {
            Runnable runnable = Bulkhead.decorateRunnable(bulkhead, () -> slow());
            new Thread(runnable).start();
        }

        Thread.sleep(10_000L);
    }

    @Test
    void testThreadPoolRegistry() throws InterruptedException {
        ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
                .coreThreadPoolSize(5)
                .maxThreadPoolSize(5)
                .build();

        ThreadPoolBulkheadRegistry registry = ThreadPoolBulkheadRegistry.ofDefaults();
        registry.addConfiguration("config", config);

        ThreadPoolBulkhead bulkhead = registry.bulkhead("pool", "config");

        for(int i = 0; i < 25; i++){
            Supplier<CompletionStage<Void>> supplier = ThreadPoolBulkhead.decorateRunnable(bulkhead, () -> slow());
            supplier.get();
        }

        Thread.sleep(10_000L);
    }
}
