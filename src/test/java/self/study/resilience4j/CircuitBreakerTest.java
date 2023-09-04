package self.study.resilience4j;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class CircuitBreakerTest {
    void callMe(){
        log.info("Hello");
        throw new RuntimeException("ups");
    }

    @Test
    void testCircuitBreaker() {
        CircuitBreaker breaker = CircuitBreaker.ofDefaults("sample");

        for(int i = 0; i < 500; i++){
            try {
                breaker.executeRunnable(() -> callMe());
            }catch (Exception e){
                log.error("error: {}", e.getMessage());
            }
        }
    }

    @Test
    void testConfig() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .failureRateThreshold(10F)
                .slidingWindowSize(10)
                .minimumNumberOfCalls(10)
                .build();

        CircuitBreaker breaker = CircuitBreaker.of("sample", config);

        for(int i = 0; i < 500; i++){
            try {
                breaker.executeRunnable(() -> callMe());
            }catch (Exception e){
                log.error("error: {}", e.getMessage());
            }
        }
    }

    @Test
    void testRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .failureRateThreshold(10F)
                .slidingWindowSize(10)
                .minimumNumberOfCalls(10)
                .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();
        registry.addConfiguration("config", config);

        CircuitBreaker breaker = registry.circuitBreaker("sample", "config");

        for(int i = 0; i < 500; i++){
            try {
                breaker.executeRunnable(() -> callMe());
            }catch (Exception e){
                log.error("error: {}", e.getMessage());
            }
        }
    }
}
