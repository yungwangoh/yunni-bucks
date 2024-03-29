package sejong.coffee.yun.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.repository.menu.MenuRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuServiceCommand {

    private final MenuRepository menuRepository;

    public Menu create(Menu menu) {
        return menuRepository.save(menu);
    }

    public Menu update() {
        return null;
    }

    @CacheEvict(value = "manu", key = "#menuId", cacheManager = "cacheManager")
    public void delete(Long menuId) {
        menuRepository.delete(menuId);
    }
}
