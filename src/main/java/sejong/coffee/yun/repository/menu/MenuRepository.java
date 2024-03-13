package sejong.coffee.yun.repository.menu;

import org.springframework.data.redis.core.RedisOperations;
import sejong.coffee.yun.domain.order.menu.Menu;

import java.util.List;

public interface MenuRepository {

    Long increaseStock(RedisOperations<String, String> redisOperations, Long menuId, int stockQuantity);
    Long decreaseStock(RedisOperations<String, String> redisOperations, Long menuId, int stockQuantity);
    Long totalIncreaseStock(RedisOperations<String, String> redisOperations, Long menuId, int stockQuantity);
    Long totalDecreaseStock(RedisOperations<String, String> redisOperations, Long menuId, int stockQuantity);
    Long usedTotalUserStockCount(RedisOperations<String, String> redisOperations, Long menuId);
    Long totalStockCount(RedisOperations<String, String> redisOperations, Long menuId);
    Menu save(Menu menu);
    Menu findById(Long id);
    List<Menu> findAll();
    void delete(Long id);
    void clear();
}
