package sejong.coffee.yun.mock.repository;

import org.springframework.boot.test.context.TestComponent;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.repository.menu.MenuRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TestComponent
public class FakeMenuRepository implements MenuRepository {

    private final Map<Long, Menu> menuList = new HashMap<>();
    private Long id = 0L;

    @Override
    public Menu save(Menu menu) {
        menuList.put(++id, menu);

        return menu;
    }

    @Override
    public Menu findById(Long id) {
        return menuList.get(id);
    }

    @Override
    public List<Menu> findAll() {
        return menuList.values().stream().toList();
    }

    @Override
    public void delete(Long id) {
        menuList.remove(id);
    }

    public void clear() {
        menuList.clear();
    }
}
