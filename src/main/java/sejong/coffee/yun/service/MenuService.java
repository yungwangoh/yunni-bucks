package sejong.coffee.yun.service;

import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.dto.menu.MenuDto;
import sejong.coffee.yun.dto.menu.MenuWrapperDto;

import java.util.List;

public interface MenuService {

    default Menu create(Menu menu) {
        return null;
    }
    default Menu update() {
        return null;
    }
    default MenuWrapperDto.Response findAll() {return null;}
    default void delete(Long menuId) {}
    default MenuDto.Response findById(Long menuId) {
        return null;
    }
    default List<MenuDto.Response> searchPopularMenus() {return null;}
}
