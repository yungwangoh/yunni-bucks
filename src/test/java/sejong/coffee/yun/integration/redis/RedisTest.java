package sejong.coffee.yun.integration.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.dto.menu.MenuDto;
import sejong.coffee.yun.integration.MainIntegrationTest;
import sejong.coffee.yun.repository.user.UserRepository;
import sejong.coffee.yun.service.command.CartServiceCommand;
import sejong.coffee.yun.service.command.MenuServiceCommand;
import sejong.coffee.yun.service.command.OrderServiceCommand;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.LongStream;

@Sql(value = "/sql/menu.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/sql/truncate.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class RedisTest extends MainIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderServiceCommand orderService;
    @Autowired
    private CartServiceCommand cartService;
    @Autowired
    private MenuServiceCommand menuService;

    private Member member;

    @BeforeEach
    void init() {
        member = userRepository.save(member());
        cartService.createCart(member.getId());
    }

    @Test
    void 인기메뉴_top_10_리스트() {
        // given
        LongStream.range(0, 9).forEach(i -> {
            cartService.addMenu(member.getId(), i + 1);
            orderService.order(member.getId(), LocalDateTime.now());
        });

        // when
        List<MenuDto.Response> responses = menuService.searchPopularMenus();

        // then
        responses.forEach(response -> System.out.println(response.title()));
    }
}
