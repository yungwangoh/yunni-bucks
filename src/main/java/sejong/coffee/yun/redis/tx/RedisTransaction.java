package sejong.coffee.yun.redis.tx;

import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;

public class RedisTransaction {

    public static void transaction(RedisOperations<String, String> operations, Command command) {
        operations.execute(new SessionCallback<Void>() {

            @Override
            public <K, V> Void execute(@NotNull RedisOperations<K, V> callbackOperations) throws DataAccessException {
                callbackOperations.multi();
                command.execute(operations);
                callbackOperations.exec();
                return null;
            }
        });
    }

    public interface Command {
        void execute(RedisOperations<String, String> operations);
    }
}
