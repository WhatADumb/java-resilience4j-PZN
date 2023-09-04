package self.study.resilience4j;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

@Slf4j
public class EventPublisherTest {
    String sayHello(){
        throw new RuntimeException("hello");
    }

    @Test
    void testMetric() {
        Retry retry = Retry.ofDefaults("sample");
        retry.getEventPublisher().onRetry(event -> log.info("pls retry"));

        try {
            Supplier<String> supplier = Retry.decorateSupplier(retry, () -> sayHello());
            supplier.get();
        }catch (Exception e){
            System.out.println(retry.getMetrics().getNumberOfFailedCallsWithoutRetryAttempt());
            System.out.println(retry.getMetrics().getNumberOfSuccessfulCallsWithoutRetryAttempt());
        }
    }

    @Test
    void testRegistry() {
        RetryRegistry registry = RetryRegistry.ofDefaults();
        registry.getEventPublisher().onEntryAdded(event -> {
            log.info("Entry added: {}", event.getAddedEntry().getName());
        });

        registry.retry("sample1");
        registry.retry("sample2");
        registry.retry("sample1");
    }
}
