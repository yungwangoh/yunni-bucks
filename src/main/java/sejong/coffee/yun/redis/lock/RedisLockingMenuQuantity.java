package sejong.coffee.yun.redis.lock;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import sejong.coffee.yun.service.CartService;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisLockingMenuQuantity {

    private final RedissonClient redissonClient;
    private final CartService cartService;

    public void menuSubQuantity(final Long key, final Long memberId, final Long menuId) {

        RLock lock = redissonClient.getLock(key.toString());

        try {
            boolean tryLock = lock.tryLock(1, 1, TimeUnit.SECONDS);

            if(!tryLock) throw new RuntimeException("fail acquire lock!!");

            cartService.addMenu(memberId, menuId);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
