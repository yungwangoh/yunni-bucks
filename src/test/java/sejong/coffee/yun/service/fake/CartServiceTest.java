package sejong.coffee.yun.service.fake;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import sejong.coffee.yun.domain.exception.DuplicatedException;
import sejong.coffee.yun.domain.exception.NotFoundException;
import sejong.coffee.yun.domain.order.menu.Beverage;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.order.menu.MenuSize;
import sejong.coffee.yun.domain.order.menu.Nutrients;
import sejong.coffee.yun.domain.user.*;
import sejong.coffee.yun.jwt.JwtProvider;
import sejong.coffee.yun.mock.repository.*;
import sejong.coffee.yun.repository.cartitem.CartItemRepository;
import sejong.coffee.yun.repository.menu.MenuRepository;
import sejong.coffee.yun.repository.user.UserRepository;
import sejong.coffee.yun.service.command.CartServiceCommand;
import sejong.coffee.yun.service.query.CartServiceQuery;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_CART;
import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_MENU;

@SpringJUnitConfig
@ContextConfiguration(classes = {
        CartServiceCommand.class,
        CartServiceQuery.class,
        FakeUserRepository.class,
        FakeOrderRepository.class,
        FakeCartRepository.class,
        FakeMenuRepository.class,
        JwtProvider.class,
        FakeCartItemRepository.class
})
@TestPropertySource(properties = {
        "jwt.key=applicationKey",
        "jwt.expireTime.access=100000",
        "jwt.expireTime.refresh=1000000",
        "jwt.blackList=blackList"
})
public class CartServiceTest {

    @Autowired
    private CartServiceQuery cartServiceQuery;
    @Autowired
    private CartServiceCommand cartServiceCommand;
    @Autowired
    private FakeCartRepository fakeCartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private FakeMenuRepository fakeMenuRepository;
    @Autowired
    private FakeCartItemRepository fakeCartItemRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    private Member member;
    private Menu menu1;
    private Menu menu2;
    private Menu menu3;
    private CartItem cartItem;
    private Cart cart;

    @BeforeEach
    void init() {
        member = Member.builder()
                .name("윤광오")
                .userRank(UserRank.BRONZE)
                .money(Money.ZERO)
                .password("qwer1234")
                .address(null)
                .email("qwer1234@naver.com")
                .orderCount(0)
                .build();

        Nutrients nutrients = new Nutrients(80, 80, 80, 80);

        Beverage beverage = Beverage.builder()
                .description("에티오피아산 커피")
                .title("커피")
                .price(Money.initialPrice(new BigDecimal(1000)))
                .nutrients(nutrients)
                .menuSize(MenuSize.M)
                .orderCount(0)
                .now(LocalDateTime.now())
                .build();

        Beverage beverage1 = Beverage.builder()
                .description("에티오피아산 커피")
                .title("커피")
                .price(Money.initialPrice(new BigDecimal(1000)))
                .nutrients(nutrients)
                .menuSize(MenuSize.M)
                .orderCount(0)
                .now(LocalDateTime.now())
                .build();

        Beverage beverage2 = Beverage.builder()
                .description("중국산 커피")
                .title("커피")
                .price(Money.initialPrice(new BigDecimal(1000)))
                .nutrients(nutrients)
                .menuSize(MenuSize.M)
                .orderCount(0)
                .now(LocalDateTime.now())
                .build();

        menu1 = beverage;
        menu2 = beverage1;
        menu3 = beverage2;

        cartItem = CartItem.builder()
                .menu(menu1)
                .build();

        cart = Cart.builder()
                .id(1L)
                .cartItems(new ArrayList<>())
                .build();
    }

    @AfterEach
    void initDB() {
        fakeCartRepository.clear();
        fakeMenuRepository.clear();
        fakeCartItemRepository.clear();
    }

    @Test
    void 카트_생성() {
        // given
        Member save = userRepository.save(member);

        // when
        Cart saveCart = cartServiceCommand.createCart(save.getId());

        // then
        assertThat(saveCart).isNotNull();
        assertThat(saveCart.getMember()).isEqualTo(save);
    }

    @Test
    void 카트가_중복일_경우() {
        // given
        Member save = userRepository.save(member);

        // when
        cartServiceCommand.createCart(save.getId());

        // then
        assertThatThrownBy(() -> cartServiceCommand.createCart(save.getId()))
                .isInstanceOf(DuplicatedException.class);
    }

    @Test
    void 없는_유저_ID로_카트를_생성_하려고_할_경우() {
        // given
        Long invalidUser = 100L;

        // when

        // then
        assertThatThrownBy(() -> cartServiceCommand.createCart(invalidUser))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 메뉴_추가() {
        // given
        Member save = userRepository.save(member);

        Menu menu = menuRepository.save(menu1);

        cartServiceCommand.createCart(save.getId());

        // when
        Cart cart = cartServiceCommand.addMenu(save.getId(), menu.getId());

        // then
        assertThat(cart.getCartItems().get(0).getMenu()).isEqualTo(menu);
    }

    @Test
    void 메뉴_추가할때_장바구니_사이즈를_넘은_경우() {
        // given
        Member save = userRepository.save(member);

        Menu menu = menuRepository.save(menu1);

        cartServiceCommand.createCart(save.getId());

        // when
        IntStream.range(0, 10).forEach(i -> cartServiceCommand.addMenu(save.getId(), menu.getId()));

        // then
        assertThatThrownBy(() -> cartServiceCommand.addMenu(save.getId(), menu.getId()))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void 유저의_카트를_찾을_수_없을_때() {
        assertThatThrownBy(() -> cartServiceQuery.findCartByMember(100L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NOT_FOUND_CART.getMessage());
    }

    @Test
    void 카트에서_메뉴_찾기() {
        // given
        Member save = userRepository.save(member);

        Menu menu = menuRepository.save(menu1);

        cartServiceCommand.createCart(save.getId());

        cartServiceCommand.addMenu(save.getId(), menu.getId());

        // when
        Menu getMenu = cartServiceQuery.getMenu(save.getId(), 0);

        // then
        assertThat(getMenu).isEqualTo(menu);
    }

    @Test
    void 카트에서_메뉴_지우기() {
        // given
        Member save = userRepository.save(member);

        Menu menu = menuRepository.save(menu1);

        cartServiceCommand.createCart(save.getId());

        cartServiceCommand.addMenu(save.getId(), menu.getId());

        // when
        Cart cart = cartServiceCommand.removeMenu(save.getId(), 0);

        // then
        assertThat(cart.getCartItems()).isEqualTo(List.of());
    }

    @Test
    void 카트에_있는_메뉴를_찾을_수_없을때() {
        // given
        Member save = userRepository.save(member);

        // when

        // then
        assertThatThrownBy(() -> cartServiceCommand.addMenu(save.getId(), 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NOT_FOUND_MENU.getMessage());
    }
}
