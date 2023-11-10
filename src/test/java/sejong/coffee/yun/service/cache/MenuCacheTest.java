package sejong.coffee.yun.service.cache;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.integration.MainIntegrationTest;
import sejong.coffee.yun.repository.menu.MenuRepository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MenuCacheTest extends MainIntegrationTest {

    @Autowired
    private MenuRepository menuRepository;

    private Menu menu;
    @PostConstruct
    void init() {
        List<Menu> menus = Stream.generate(() -> menuRepository.save(bread())).limit(20)
                .toList();

        menu = menus.get(15);
    }

    @Test
    void 메뉴_단건_조회() throws Exception {

        ResultActions resultActions = null;
        for(int i = 0; i < 1000; i++) {
            resultActions = mockMvc.perform(get(MENU_API_PATH + "/{menuId}", menu.getId()));
            resultActions.andExpect(status().isOk());
        }

        resultActions.andDo(print());
    }

    @Test
    void 메뉴_모두_조회_캐시() throws Exception {

        for(int i = 0; i < 100; i++) {
            ResultActions resultActions = mockMvc.perform(get(MENU_API_PATH));
            resultActions.andExpect(status().isOk());
        }
    }
}
