package sejong.coffee.yun.service.fake;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import sejong.coffee.yun.domain.exception.NotFoundException;
import sejong.coffee.yun.domain.order.menu.*;
import sejong.coffee.yun.domain.user.Cart;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.domain.user.UserRank;
import sejong.coffee.yun.jwt.JwtProvider;
import sejong.coffee.yun.mock.repository.FakeMenuRepository;
import sejong.coffee.yun.mock.repository.FakeOrderRepository;
import sejong.coffee.yun.mock.repository.FakeUserRepository;
import sejong.coffee.yun.repository.cart.fake.FakeCartRepository;
import sejong.coffee.yun.repository.menu.MenuRepository;
import sejong.coffee.yun.repository.user.UserRepository;
import sejong.coffee.yun.service.CartService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_CART;
import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_MENU;

@SpringJUnitConfig
@ContextConfiguration(classes = {
        CartService.class,
        FakeUserRepository.class,
        FakeOrderRepository.class,
        FakeCartRepository.class,
        FakeMenuRepository.class,
        JwtProvider.class
})
@TestPropertySource(properties = {
        "jwt.key=applicationKey",
        "jwt.expireTime.access=100000",
        "jwt.expireTime.refresh=1000000",
        "jwt.blackList=blackList"
})
public class CartServiceTest {

    @Autowired
    private CartService cartService;
    @Autowired
    private FakeCartRepository fakeCartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private FakeMenuRepository fakeMenuRepository;

    private Member member;
    private Menu menu1;
    private Menu menu2;
    private Menu menu3;

    @BeforeEach
    void init() {
        member = Member.builder()
                .name("윤광오")
                .userRank(UserRank.BRONZE)
                .money(Money.ZERO)
                .password("qwer1234")
                .address(null)
                .email("qwer1234@naver.com")
                .build();

        Nutrients nutrients = new Nutrients(80, 80, 80, 80);

        Beverage beverage = Beverage.builder()
                .description("에티오피아산 커피")
                .title("커피")
                .price(Money.initialPrice(new BigDecimal(1000)))
                .nutrients(nutrients)
                .menuSize(MenuSize.M)
                .now(LocalDateTime.now())
                .build();

        menu1 = beverage;
        menu2 = beverage;
        menu3 = beverage;
    }

    @AfterEach
    void initDB() {
        fakeCartRepository.clear();
        fakeMenuRepository.clear();
    }

    @Test
    void 카트_생성() {
        // given
        Member save = userRepository.save(member);

        // when
        Cart saveCart = cartService.createCart(save.getId());

        // then
        assertThat(saveCart).isNotNull();
        assertThat(saveCart.getMember()).isEqualTo(save);
    }

    @Test
    void 메뉴_추가() {
        // given
        Member save = userRepository.save(member);

        Menu menu = menuRepository.save(menu1);

        cartService.createCart(save.getId());

        // when
        Cart cart = cartService.addMenu(save.getId(), menu.getId());

        // then
        assertThat(cart.getMember()).isEqualTo(save);
        assertThat(cart.getMenuList()).isEqualTo(List.of(menu));
    }

    @Test
    void 유저의_카트를_찾을_수_없을_때() {
        assertThatThrownBy(() -> cartService.findCartByMember(100L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NOT_FOUND_CART.getMessage());
    }

    @Test
    void 카트에서_메뉴_찾기() {
        // given
        Member save = userRepository.save(member);

        Menu menu = menuRepository.save(menu1);

        cartService.createCart(save.getId());

        cartService.addMenu(save.getId(), menu.getId());

        // when
        Menu getMenu = cartService.getMenu(save.getId(), 0);

        // then
        assertThat(getMenu).isEqualTo(menu);
    }

    @Test
    void 카트에서_메뉴_지우기() {
        // given
        Member save = userRepository.save(member);

        Menu menu = menuRepository.save(menu1);

        cartService.createCart(save.getId());

        cartService.addMenu(save.getId(), menu.getId());

        // when
        Cart cart = cartService.removeMenu(save.getId(), 0);

        // then
        assertThat(cart.getMenuList()).isEqualTo(List.of());
    }

    @Test
    void 카트에_있는_메뉴를_찾을_수_없을때() {
        // given
        Member save = userRepository.save(member);

        // when
        cartService.createCart(save.getId());

        // then
        assertThatThrownBy(() -> cartService.addMenu(save.getId(), 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NOT_FOUND_MENU.getMessage());
    }
}
