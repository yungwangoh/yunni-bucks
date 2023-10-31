package sejong.coffee.yun.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.dto.menu.MenuDto;
import sejong.coffee.yun.repository.menu.MenuRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    @Transactional
    public Menu create(Menu menu) {
        return menuRepository.save(menu);
    }

    @Cacheable(value = "menu", key = "#menuId", cacheManager = "cacheManager")
    public MenuDto.Response findById(Long menuId) {
        return new MenuDto.Response(menuRepository.findById(menuId));
    }

    public List<MenuDto.Response> findAll() {
        return menuRepository.findAll().stream().map(MenuDto.Response::new).toList();
    }

    @Transactional
    @Deprecated
    public Menu update() {
        return null;
    }

    @Transactional
    @CacheEvict(value = "manu", key = "#menuId", cacheManager = "cacheManager")
    public void delete(Long menuId) {
        menuRepository.delete(menuId);
    }
}
