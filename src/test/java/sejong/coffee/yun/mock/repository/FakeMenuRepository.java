package sejong.coffee.yun.mock.repository;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Repository;
import sejong.coffee.yun.domain.order.menu.Beverage;
import sejong.coffee.yun.domain.order.menu.Bread;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.repository.menu.MenuRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_MENU;

@Repository
public class FakeMenuRepository implements MenuRepository {

    private final List<Menu> menuList = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong id = new AtomicLong(0);

    @Override
    public Menu save(Menu menu) {
        if(menu.getId() == null || menu.getId() == 0L) {
            if (menu instanceof Bread bread) {
                Bread newBread = Bread.from(id.incrementAndGet(), bread);
                menuList.add(newBread);

                return newBread;
            } else if (menu instanceof Beverage beverage) {
                Beverage newBeverage = Beverage.from(id.incrementAndGet(), beverage);
                menuList.add(newBeverage);

                return newBeverage;
            }
        }
        menuList.removeIf(m -> Objects.equals(m.getId(), menu.getId()));
        menuList.add(menu);
        return menu;
    }

    @Override
    public Menu findById(Long id) {
        return menuList.stream()
                .filter(menu -> Objects.equals(menu.getId(), id))
                .findAny()
                .orElseThrow(NOT_FOUND_MENU::notFoundException);
    }

    @Override
    public List<Menu> findAll() {
        return menuList;
    }

    @Override
    public void delete(Long id) {
        menuList.remove(Math.toIntExact(id));
    }

    @Override
    public void clear() {
        menuList.clear();
    }

    @Override
    public Long increaseStock(RedisOperations<String, String> redisOperations, Long menuId, int stockQuantity) {
        return null;
    }

    @Override
    public Long decreaseStock(RedisOperations<String, String> redisOperations, Long menuId, int stockQuantity) {
        return null;
    }

    @Override
    public Long totalIncreaseStock(RedisOperations<String, String> redisOperations, Long menuId, int stockQuantity) {
        return null;
    }

    @Override
    public Long totalDecreaseStock(RedisOperations<String, String> redisOperations, Long menuId, int stockQuantity) {
        return null;
    }

    @Override
    public Long usedTotalUserStockCount(RedisOperations<String, String> redisOperations, Long menuId) {
        return null;
    }

    @Override
    public Long totalStockCount(RedisOperations<String, String> redisOperations, Long menuId) {
        return null;
    }
}
