package sejong.coffee.yun.repository.menu;

import sejong.coffee.yun.domain.order.menu.Menu;

import java.util.List;

public interface MenuRepository {

    Menu save(Menu menu);
    Menu findById(Long id);
    List<Menu> findAll();
    void delete(Long id);
}
