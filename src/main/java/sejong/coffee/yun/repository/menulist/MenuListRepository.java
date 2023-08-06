package sejong.coffee.yun.repository.menulist;

import sejong.coffee.yun.domain.order.MenuList;

import java.util.List;

public interface MenuListRepository {

    MenuList save(MenuList menuList);
    MenuList findById(Long id);
    List<MenuList> findAll();
    void delete(Long id);
}
