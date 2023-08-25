package sejong.coffee.yun.repository.menu.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
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

    @Override
    @Transactional
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
    @Transactional
    public void delete(Long id) {
        jpaMenuRepository.deleteById(id);
    }

    @Override
    public void clear() {
        jpaMenuRepository.deleteAll();
    }
}
