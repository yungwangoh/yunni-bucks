package sejong.coffee.yun.repository.menulist.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sejong.coffee.yun.domain.order.MenuList;
import sejong.coffee.yun.repository.menulist.MenuListRepository;
import sejong.coffee.yun.repository.menulist.jpa.JpaMenuListRepository;

import java.util.List;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_MENU_LIST;

@Repository
@RequiredArgsConstructor
public class MenuListRepositoryImpl implements MenuListRepository {

    private final JpaMenuListRepository jpaMenuListRepository;

    @Override
    public MenuList save(MenuList menuList) {
        return jpaMenuListRepository.save(menuList);
    }

    @Override
    public MenuList findById(Long id) {
        return jpaMenuListRepository.findById(id)
                .orElseThrow(NOT_FOUND_MENU_LIST::notFoundException);
    }

    @Override
    public List<MenuList> findAll() {
        return jpaMenuListRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        jpaMenuListRepository.deleteById(id);
    }
}
