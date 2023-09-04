package self.study.resilience4j;

import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

@Slf4j
public class RetryTest {
    void sayHello(){
      log.info("Hello Hello");
      throw new IllegalArgumentException("check it out");
    }

    String hello(){
        log.info("Hello There");
        throw new RuntimeException("Check it in");
    }

    @Test
    void testRetryRunnable() {
        Retry retry = Retry.ofDefaults("sample");
        retry.executeRunnable(() -> sayHello());
    }

    @Test
    void testRetrySupplier() {
        Retry retry = Retry.ofDefaults("sample");
        String supplier = retry.executeSupplier(() -> hello());
        System.out.println(supplier);
    }

    @Test
    void testRetryRunnable1() {
        Retry retry = Retry.ofDefaults("sample");
        Runnable runnable = Retry.decorateRunnable(retry, () -> sayHello());
        runnable.run();
    }

    @Test
    void testRetrySupplier1() {
        Retry retry = Retry.ofDefaults("sample");
        Supplier<String> stringSupplier = Retry.decorateSupplier(retry, () -> hello());

        stringSupplier.get();
    }
}
