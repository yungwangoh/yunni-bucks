package sejong.coffee.yun.service.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import sejong.coffee.yun.dto.menu.MenuDto;
import sejong.coffee.yun.integration.MainIntegrationTest;
import sejong.coffee.yun.service.MenuService;

import java.util.List;

@Sql(value = "/sql/menu.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/sql/truncate.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class RedisTest extends MainIntegrationTest {

    @Autowired
    private MenuService menuService;

    @Test
    void 인기메뉴_TOP_10_테스트() {
        // given

        // when
        List<MenuDto.Response> responses = menuService.searchPopularMenus();

        // then
        responses.forEach(response -> System.out.println("response :" + response.title()));
    }
}
