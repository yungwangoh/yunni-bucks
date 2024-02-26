package sejong.coffee.yun.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.dto.menu.MenuDto;
import sejong.coffee.yun.dto.menu.MenuWrapperDto;
import sejong.coffee.yun.repository.menu.MenuRepository;
import sejong.coffee.yun.service.MenuService;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_MENU;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuServiceQuery implements MenuService {

    private final MenuRepository menuRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String RANK_KEY = "rank";

    @Override
    //@Cacheable(value = "menu", key = "#menuId", cacheManager = "cacheManager")
    public MenuDto.Response findById(Long menuId) {
        return new MenuDto.Response(menuRepository.findById(menuId));
    }

    @Cacheable(value = "menu", cacheManager = "cacheManager")
    public MenuWrapperDto.Response findAll() {

        List<MenuDto.Response> responses = menuRepository.findAll().stream().map(MenuDto.Response::new).toList();

        return new MenuWrapperDto.Response(responses);
    }
    @Override
    public List<MenuDto.Response> searchPopularMenus() {

        List<Menu> menus = menuRepository.findAll();

        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();

        Set<ZSetOperations.TypedTuple<String>> rankSets =
                zSetOperations.reverseRangeByScoreWithScores(RANK_KEY, 0, 10);

        assert rankSets != null;

        return rankSets.stream().map(rankSet -> getMenuDtoResponse(menus, rankSet.getValue())).toList();
    }

    private MenuDto.Response getMenuDtoResponse(List<Menu> menus, String rankSet) {
        return menus.stream().filter(response -> Objects.equals(response.getTitle(), rankSet))
                .findAny().map(MenuDto.Response::new).orElseThrow(NOT_FOUND_MENU::notFoundException);
    }
}
