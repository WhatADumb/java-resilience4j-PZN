package self.study.resilience4j;

import io.github.resilience4j.retry.Retry;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

public class MetricTest {
    String sayHello(){
        throw new RuntimeException("hello");
    }

    @Test
    void testMetric() {
        Retry retry = Retry.ofDefaults("sample");

        try {
            Supplier<String> supplier = Retry.decorateSupplier(retry, () -> sayHello());
            supplier.get();
        }catch (Exception e){
            System.out.println(retry.getMetrics().getNumberOfFailedCallsWithoutRetryAttempt());
            System.out.println(retry.getMetrics().getNumberOfSuccessfulCallsWithoutRetryAttempt());
        }
    }
}
