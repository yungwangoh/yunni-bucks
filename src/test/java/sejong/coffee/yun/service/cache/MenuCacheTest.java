package sejong.coffee.yun.service.cache;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;
import sejong.coffee.yun.integration.MainIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(value = "/sql/menu.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/sql/truncate.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class MenuCacheTest extends MainIntegrationTest {

    @Test
    void 메뉴_단건_조회_캐시() throws Exception {

        for(int i = 0; i < 100; i++) {
            ResultActions resultActions = mockMvc.perform(get(MENU_API_PATH + "/{menuId}", 15L));
            resultActions.andExpect(status().isOk());
        }
    }

    @Test
    void 메뉴_모두_조회_캐시() throws Exception {

        for(int i = 0; i < 100; i++) {
            ResultActions resultActions = mockMvc.perform(get(MENU_API_PATH));
            resultActions.andExpect(status().isOk());
        }
    }
}
