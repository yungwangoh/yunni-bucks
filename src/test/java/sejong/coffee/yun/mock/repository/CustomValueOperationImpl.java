package sejong.coffee.yun.mock.repository;

import org.springframework.boot.test.context.TestComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@TestComponent
public class CustomValueOperationImpl implements CustomValueOperation {

    private final Map<String, String> map = new HashMap<>();

    @Override
    public void set(String key, String value) {
        map.put(key, value);
    }

    @Override
    public void set(String key, String value, long timeout, TimeUnit timeUnit) {

        map.put(key, value);

        await(key, timeout, timeUnit);
    }

    @Override
    public String get(String key) {
        return map.get(key);
    }

    @Override
    public void remove(String key) {
        map.remove(key);
    }

    private void await(String key, long timeout, TimeUnit timeUnit) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.submit(() -> {
            try {
                timeUnit.sleep(timeout);
                map.remove(key);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
