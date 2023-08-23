package sejong.coffee.yun.mock.repository;

import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public interface CustomValueOperation {

    void set(String key, String value);
    void set(String key, String value, long timeout, TimeUnit timeUnit);
    default void set(String key, String value, Duration duration) {

        Assert.notNull(duration, "Timeout must not be null!");

        if(TimeoutUtils.hasMillis(duration)) {
            set(key, value, duration.toMillis(), TimeUnit.MILLISECONDS);
        } else {
            set(key, value, duration.getSeconds(), TimeUnit.SECONDS);
        }
    }
    String get(String key);
    void remove(String key);
    void clear();
}
