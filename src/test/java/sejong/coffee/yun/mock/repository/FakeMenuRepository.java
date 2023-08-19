package sejong.coffee.yun.mock.repository;

import org.springframework.boot.test.context.TestComponent;
import sejong.coffee.yun.domain.order.menu.Beverage;
import sejong.coffee.yun.domain.order.menu.Bread;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.repository.menu.MenuRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_MENU;

@TestComponent
public class FakeMenuRepository implements MenuRepository {

    private final List<Menu> menuList = new ArrayList<>();
    private Long id = 0L;

    @Override
    public Menu save(Menu menu) {
        if(menu.getId() == null || menu.getId() == 0L) {
            if (menu instanceof Bread bread) {
                Bread newBread = Bread.from(++id, bread);
                menuList.add(newBread);

                return newBread;
            } else if (menu instanceof Beverage beverage) {
                Beverage newBeverage = Beverage.from(++id, beverage);
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

    public void clear() {
        menuList.clear();
    }
}
