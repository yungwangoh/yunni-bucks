package sejong.coffee.yun.repository.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@Primary
@RequiredArgsConstructor
public class RedisRepository implements NoSqlRepository {

    private final RedisTemplate<String, String> template;

    @Override
    public void setValues(String key, String value) {
        ValueOperations<String, String> valueOperations = template.opsForValue();
        valueOperations.set(key, value);
    }

    @Override
    public void setValues(String key, String value, Duration duration) {
        ValueOperations<String, String> valueOperations = template.opsForValue();
        valueOperations.set(key, value, duration);
    }

    @Override
    public String getValues(String key) {
        ValueOperations<String, String> valueOperations = template.opsForValue();
        return valueOperations.get(key);
    }

    @Override
    public void deleteValues(String key) {
        template.delete(key);
    }

    @Override
    public void clear() {
        template.execute((RedisCallback<Object>) connection -> {
            connection.flushDb();
            return null;
        });
    }
}
