package self.study.resilience4j;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class RetryRegistryTest {
    void callMe(){
        System.out.println("Hello There");
        throw new RuntimeException("501");
    }

    @Test
    void testRegister() {
        RetryRegistry registry = RetryRegistry.ofDefaults();

        Retry retry1 = registry.retry("sample-0");
        Retry retry2 = registry.retry("sample-0");
        Assertions.assertSame(retry1, retry2);
    }

    @Test
    void testRegistryConfig() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(4)
                .waitDuration(Duration.ofSeconds(3))
                .build();

        RetryRegistry registry = RetryRegistry.ofDefaults();
        registry.addConfiguration("config", config);

        Retry retry1 = registry.retry("sample-0", "config");
        Retry retry2 = registry.retry("sample-0", "config");

        Runnable runnable = Retry.decorateRunnable(retry1, () -> callMe());
        runnable.run();
    }
}
