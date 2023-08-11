package sejong.coffee.yun.mapper;

import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;

@Component
@Slf4j
public class CustomMapper {

    public <T> T map(Object entity, Class<T> type) {

        Assert.notNull(entity, "entity must not be null!!");
        Assert.notNull(type, "type must not be null!!");

        try {
            Constructor<T> constructor = type.getDeclaredConstructor(entity.getClass());

            return constructor.newInstance(entity);
        } catch (Exception e) {
            log.error("error = {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
