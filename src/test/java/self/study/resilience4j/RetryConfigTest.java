package self.study.resilience4j;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;

@Slf4j
public class RetryConfigTest {
    void greetings(){
        log.info("Hello, World!!!");
        throw new RuntimeException("200");
    }

    @Test
    void testConfig() {
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(4)
                .waitDuration(Duration.ofSeconds(3))
                .retryExceptions(RuntimeException.class)
                .build();

        Retry retry = Retry.of("sample", retryConfig);
        retry.executeRunnable(() -> greetings());
    }
}
