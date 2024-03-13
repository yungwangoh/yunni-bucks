package sejong.coffee.yun.repository.menu.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Repository;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.repository.menu.MenuRepository;
import sejong.coffee.yun.repository.menu.jpa.JpaMenuRepository;

import java.util.List;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_MENU;

@Repository
@Primary
@RequiredArgsConstructor
public class MenuRepositoryImpl implements MenuRepository {

    private final JpaMenuRepository jpaMenuRepository;
    private static final String REDIS_TOTAL_STOCK_KEY = "total:";
    private static final String REDIS_STOCK_KEY = "stock:";

    @Override
    public Menu save(Menu menu) {
        return jpaMenuRepository.save(menu);
    }

    @Override
    public Menu findById(Long id) {
        return jpaMenuRepository.findById(id)
                .orElseThrow(NOT_FOUND_MENU::notFoundException);
    }

    @Override
    public List<Menu> findAll() {
        return jpaMenuRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        jpaMenuRepository.deleteById(id);
    }

    @Override
    public void clear() {
        jpaMenuRepository.deleteAll();
    }

    /**
     * 현재 상품 재고 증가
     * @param menuId
     * @param stockQuantity
     */
    @Override
    public Long increaseStock(RedisOperations<String, String> redisOperations, Long menuId, int stockQuantity) {
        String key = createKey(menuId);

        return redisOperations.opsForValue().increment(key, stockQuantity);
    }

    @Override
    public Long decreaseStock(RedisOperations<String, String> redisOperations, Long menuId, int stockQuantity) {
        String key = createKey(menuId);

        return redisOperations.opsForValue().decrement(key, stockQuantity);
    }

    /**
     * 전체 재고의 증가
     * @param menuId
     */
    @Override
    public Long totalIncreaseStock(RedisOperations<String, String> redisOperations, Long menuId, int stockQuantity) {
        String totalKey = createTotalKey(menuId);

        return redisOperations.opsForValue().increment(totalKey, stockQuantity);
    }

    @Override
    public Long totalDecreaseStock(RedisOperations<String, String> redisOperations, Long menuId, int stockQuantity) {
        String totalKey = createTotalKey(menuId);

        return redisOperations.opsForValue().decrement(totalKey, stockQuantity);
    }

    /**
     * 전체 재고의 개수 반환
     * @param menuId
     * @return
     */
    @Override
    public Long totalStockCount(RedisOperations<String, String> redisOperations, Long menuId) {
        String key = createTotalKey(menuId);

        String get = redisOperations.opsForValue().get(key);

        return get == null ? 0L : Long.parseLong(get);
    }

    @Override
    public Long usedTotalUserStockCount(RedisOperations<String, String> redisOperations, Long menuId) {
        String key = createKey(menuId);

        String get = redisOperations.opsForValue().get(key);

        return get == null ? 0L : Long.parseLong(get);
    }

    private String createKey(Long menuId) {
        return REDIS_STOCK_KEY + menuId;
    }

    private String createTotalKey(Long menuId) {
        return REDIS_TOTAL_STOCK_KEY + menuId;
    }
}
