package sejong.coffee.yun.domain.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sejong.coffee.yun.domain.discount.condition.CouponCondition;
import sejong.coffee.yun.domain.discount.condition.RankCondition;
import sejong.coffee.yun.domain.discount.policy.PercentPolicy;
import sejong.coffee.yun.domain.exception.MenuException;
import sejong.coffee.yun.domain.order.menu.*;
import sejong.coffee.yun.domain.user.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static sejong.coffee.yun.domain.exception.ExceptionControl.EMPTY_MENUS;

class OrderTest {

    private final Calculator calculator;

    public OrderTest() {
        this.calculator = new Calculator(new PercentPolicy(new RankCondition(), new CouponCondition()));
    }

    private Menu menu1;
    private Menu menu2;
    private Menu menu3;
    private Coupon coupon;
    private List<CartItem> menuList;
    private Cart cart;
    private Member member;

    @BeforeEach
    void init() {
        Nutrients nutrients = new Nutrients(80, 80, 80, 80);

        menu1 = Beverage.builder()
                .description("에티오피아산 커피")
                .title("커피")
                .price(Money.initialPrice(new BigDecimal(1000)))
                .nutrients(nutrients)
                .menuSize(MenuSize.M)
                .now(LocalDateTime.now())
                .quantity(100)
                .build();

        menu2 = Bread.builder()
                .description("커피빵")
                .title("빵")
                .price(Money.initialPrice(new BigDecimal(1000)))
                .nutrients(nutrients)
                .menuSize(MenuSize.M)
                .now(LocalDateTime.now())
                .quantity(100)
                .build();

        menu3 = Bread.builder()
                .description("소라빵")
                .title("빵")
                .price(Money.initialPrice(new BigDecimal(1200)))
                .nutrients(nutrients)
                .menuSize(MenuSize.M)
                .now(LocalDateTime.now())
                .quantity(100)
                .build();

        coupon = Coupon.builder()
            .createAt(LocalDateTime.of(2023, 7, 29, 10, 10))
            .discountRate(0.1)
            .name("신규가입 쿠폰")
            .identityNumber("1234-1234-1234-1234")
            .couponUse(CouponUse.NO)
            .expireAt(LocalDateTime.of(2024, 7, 29, 10, 10))
            .build();

        menuList = List.of(
                CartItem.builder().menu(menu1).build(),
                CartItem.builder().menu(menu2).build(),
                CartItem.builder().menu(menu3).build()
        );

        member = Member.builder()
                .name("윤광오")
                .userRank(UserRank.SILVER)
                .money(Money.ZERO)
                .password("qwer1234")
                .address(null)
                .email("qwer1234@naver.com")
                .orderCount(0)
                .coupon(coupon)
                .build();

        cart = Cart.builder()
                .member(member)
                .cartItems(menuList)
                .build();
    }

    @Test
    void 주문_총_금액() {
        // given
        Money money = calculator.calculateMenus(member, cart.convertToMenus());

        money.mapBigDecimalToLong();

        // when
        Order order = Order.createOrder(cart, money, LocalDateTime.now());

        // then
        assertThat(order.getOrderPrice().getTotalPrice()).isEqualTo(money.getTotalPrice());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDER);
    }

    @Test
    void 주문명_확인() {
        // given
        Money money = calculator.calculateMenus(member, cart.convertToMenus());

        // when
        Order order = Order.createOrder(cart, money, LocalDateTime.now());
        String orderName = menuList.get(0).getMenu().getTitle() + " 외 " + menuList.size() + "개";

        // then
        assertThat(order.getName()).isEqualTo(orderName);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDER);
    }

    @Test
    void 메뉴가_비어있을_때_주문명을_만들지_못함() {
        // given
        Cart c = Cart.builder()
                .member(member)
                .cartItems(new ArrayList<>())
                .build();

        // when

        // then
        assertThatThrownBy(() -> Order.createOrder(c, Money.ZERO, LocalDateTime.now()))
                .isInstanceOf(MenuException.class)
                .hasMessageContaining(EMPTY_MENUS.getMessage());
    }

    @Test
    void 주문_취소_했을때_상태_주문취소로_변경() {
        // given
        Money money = calculator.calculateMenus(member, cart.convertToMenus());
        Order order = Order.createOrder(cart, money, LocalDateTime.now());

        // when
        order.cancel();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCEL);
    }

    @Test
    void 주문하고_쿠폰사용_상태_확인() {
        // given
        Money money = calculator.calculateMenus(member, cart.convertToMenus());

        // when
        Order.createOrder(cart, money, LocalDateTime.now());

        // then
        assertThat(member.getCoupon().getCouponUse()).isEqualTo(CouponUse.YES);
    }

    @Test
    void 다양한_할인_적용_후_주문_총_금액_확인() {
        // given
        Money money = calculator.calculateMenus(member, cart.convertToMenus());

        money.mapBigDecimalToLong();

        // when
        Order order = Order.createOrder(cart, money, LocalDateTime.now());

        // then
        assertThat(order.fetchTotalOrderPrice()).isEqualTo(money.getTotalPrice());
    }

    @Test
    void 주문하고_메뉴_수량이_감소_됐는지_확인() {
        // given
        int totalQuantity = 100;
        Money money = calculator.calculateMenus(member, cart.convertToMenus());

        // when
        Order order = Order.createOrder(cart, money, LocalDateTime.now());

        order.getCart().getCartItems().stream().map(CartItem::getMenu)
                .forEach(Menu::subQuantity);

        List<Menu> menus = order.getCart().getCartItems().stream().map(CartItem::getMenu)
                .toList();

        // then
       menus.forEach(menu -> {
           assertThat(menu.getQuantity()).isEqualTo(totalQuantity - 1);
       });
    }
}