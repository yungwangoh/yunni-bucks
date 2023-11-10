package sejong.coffee.yun.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.dto.menu.MenuDto;
import sejong.coffee.yun.dto.menu.MenuWrapperDto;
import sejong.coffee.yun.repository.menu.MenuRepository;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_MENU;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String RANK_KEY = "rank";

    @Transactional
    public Menu create(Menu menu) {
        return menuRepository.save(menu);
    }

    //@Cacheable(value = "menu", key = "#menuId", cacheManager = "cacheManager")
    public MenuDto.Response findById(Long menuId) {
        return new MenuDto.Response(menuRepository.findById(menuId));
    }

    @Cacheable(value = "menu", cacheManager = "cacheManager")
    public MenuWrapperDto.Response findAll() {

        List<MenuDto.Response> responses = menuRepository.findAll().stream().map(MenuDto.Response::new).toList();

        return new MenuWrapperDto.Response(responses);
    }

    public List<MenuDto.Response> searchPopularMenus() {

        List<Menu> menus = menuRepository.findAll();

        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();

        Set<ZSetOperations.TypedTuple<String>> rankSets =
                zSetOperations.reverseRangeByScoreWithScores(RANK_KEY, 0, 10);

        assert rankSets != null;

        return rankSets.stream().map(rankSet -> getMenuDtoResponse(menus, rankSet.getValue())).toList();
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

    private MenuDto.Response getMenuDtoResponse(List<Menu> menus, String rankSet) {
        return menus.stream().filter(response -> Objects.equals(response.getTitle(), rankSet))
                .findAny().map(MenuDto.Response::new).orElseThrow(NOT_FOUND_MENU::notFoundException);
    }
}
