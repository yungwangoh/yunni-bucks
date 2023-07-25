package sejong.coffee.yun.domain.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sejong.coffee.yun.domain.discount.condition.RankCondition;
import sejong.coffee.yun.domain.discount.policy.PercentPolicy;
import sejong.coffee.yun.domain.exception.MenuException;
import sejong.coffee.yun.domain.order.menu.*;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.domain.user.User;
import sejong.coffee.yun.domain.user.UserRank;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static sejong.coffee.yun.domain.exception.ExceptionControl.EMPTY_MENUS;

class OrderTest {

    private Calculator calculator;

    public OrderTest() {
        this.calculator = new Calculator(new PercentPolicy(new RankCondition()));
    }

    private Menu menu1;
    private Menu menu2;
    private Menu menu3;

    @BeforeEach
    void init() {
        Nutrients nutrients = new Nutrients(80, 80, 80, 80);

        menu1 = new Beverage("커피", "에티오피아산 커피",
                Money.initialPrice(new BigDecimal(1000)), nutrients, MenuSize.M);
        menu2 = new Beverage("아이스티", "복숭아 아이스티",
                Money.initialPrice(new BigDecimal(1000)), nutrients, MenuSize.M);
        menu3 = new Bread("소라빵", "소라빵",
                Money.initialPrice(new BigDecimal(1000)), nutrients, MenuSize.M);
    }

    @Test
    void 주문_총_금액() {
        // given
        User user = User.builder()
                .name("윤광오")
                .userRank(UserRank.BRONZE)
                .money(Money.ZERO)
                .password("qwer1234")
                .address(null)
                .email("qwer1234@naver.com")
                .build();

        MenuList menuList = new MenuList(List.of(menu1, menu2, menu3));

        Money money = calculator.calculateMenus(user, menuList.getMenus());

        // when
        Order order = Order.createOrder(user, menuList, money);

        // then
        assertThat(order.getOrderPrice().getTotalPrice()).isEqualTo("3000.0");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDER);
    }

    @Test
    void 주문명_확인() {
        // given
        User user = User.builder()
                .name("윤광오")
                .userRank(UserRank.BRONZE)
                .money(Money.ZERO)
                .password("qwer1234")
                .address(null)
                .email("qwer1234@naver.com")
                .build();

        MenuList menuList = new MenuList(List.of(menu1, menu2, menu3));

        Money money = calculator.calculateMenus(user, menuList.getMenus());

        // when
        Order order = Order.createOrder(user, menuList, money);
        String orderName = menuList.getMenus().get(0).getTitle() + " 외 " + menuList.getMenus().size() + "개";

        // then
        assertThat(order.getName()).isEqualTo(orderName);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDER);
    }

    @Test
    void 메뉴가_비어있을_때_주문명을_만들지_못함() {
        // given
        User user = User.builder()
                .name("윤광오")
                .userRank(UserRank.BRONZE)
                .money(Money.ZERO)
                .password("qwer1234")
                .address(null)
                .email("qwer1234@naver.com")
                .build();

        // when
        MenuList menuList = new MenuList(List.of());

        // then
        assertThatThrownBy(() -> Order.createOrder(user, menuList, Money.ZERO))
                .isInstanceOf(MenuException.class)
                .hasMessageContaining(EMPTY_MENUS.getMessage());
    }

    @Test
    void 주문_취소_했을때_상태_주문취소로_변경() {
        // given
        User user = User.builder()
                .name("윤광오")
                .userRank(UserRank.BRONZE)
                .money(Money.ZERO)
                .password("qwer1234")
                .address(null)
                .email("qwer1234@naver.com")
                .build();

        MenuList menuList = new MenuList(List.of(menu1, menu2, menu3));

        Money money = calculator.calculateMenus(user, menuList.getMenus());
        Order order = Order.createOrder(user, menuList, money);

        // when
        order.cancel();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCEL);
    }
}