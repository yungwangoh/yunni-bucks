package sejong.coffee.yun.repository.redis;

import java.time.Duration;

public interface NoSqlRepository {

    void setValues(String key, String value);
    void setValues(String key, String value, Duration duration);
    String getValues(String key);
    void deleteValues(String key);
    void clear();
}
