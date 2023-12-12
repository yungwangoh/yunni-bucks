package sejong.coffee.yun.integration.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.dto.menu.MenuDto;
import sejong.coffee.yun.integration.MainIntegrationTest;
import sejong.coffee.yun.repository.user.UserRepository;
import sejong.coffee.yun.service.CartService;
import sejong.coffee.yun.service.MenuService;
import sejong.coffee.yun.service.OrderService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

//@Sql(value = "/sql/menu.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//@Sql(value = "/sql/truncate.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class RedisTest extends MainIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;
    @Autowired
    private MenuService menuService;

    private Member member;

    @BeforeEach
    void init() {
        member = userRepository.save(member());
        //cartService.createCart(member.getId());
    }

    @Test
    void 인기메뉴_top_10_리스트() {
        // given
        IntStream.range(0, 10).forEach(i -> {
            cartService.addMenu(member.getId(), 5L);
            orderService.order(member.getId(), LocalDateTime.now());
        });

        IntStream.range(0, 10).forEach(i -> {
            cartService.addMenu(member.getId(), 10L);
            orderService.order(member.getId(), LocalDateTime.now());
        });

        IntStream.range(0, 10).forEach(i -> {
            cartService.addMenu(member.getId(), 15L);
            orderService.order(member.getId(), LocalDateTime.now());
        });

        // when
        List<MenuDto.Response> responses = menuService.searchPopularMenus();

        // then
        responses.forEach(response -> System.out.println(response.title()));
    }
}
