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

    private Calculator calculator;

    public OrderTest() {
        this.calculator = new Calculator(new PercentPolicy(new RankCondition(), new CouponCondition()));
    }

    private Menu menu1;
    private Menu menu2;
    private Menu menu3;
    private Coupon coupon;
    private List<Menu> menuList;

    @BeforeEach
    void init() {
        Nutrients nutrients = new Nutrients(80, 80, 80, 80);

        menu1 = new Beverage("커피", "에티오피아산 커피",
                Money.initialPrice(new BigDecimal(1000)), nutrients, MenuSize.M);
        menu2 = new Beverage("아이스티", "복숭아 아이스티",
                Money.initialPrice(new BigDecimal(1000)), nutrients, MenuSize.M);
        menu3 = new Bread("소라빵", "소라빵",
                Money.initialPrice(new BigDecimal(1000)), nutrients, MenuSize.M);

        coupon = Coupon.builder()
            .createAt(LocalDateTime.of(2023, 7, 29, 10, 10))
            .discountRate(0.1)
            .name("신규가입 쿠폰")
            .identityNumber("1234-1234-1234-1234")
            .couponUse(CouponUse.NO)
            .expireAt(LocalDateTime.of(2024, 7, 29, 10, 10))
            .build();

        menuList = List.of(menu1, menu2, menu3);
    }

    @Test
    void 주문_총_금액() {
        // given
        Member member = Member.builder()
                .name("윤광오")
                .userRank(UserRank.BRONZE)
                .money(Money.ZERO)
                .password("qwer1234")
                .address(null)
                .email("qwer1234@naver.com")
                .build();

        Money money = calculator.calculateMenus(member, menuList);

        // when
        Order order = Order.createOrder(member, menuList, money);

        // then
        assertThat(order.getOrderPrice().getTotalPrice()).isEqualTo("3000.0");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDER);
    }

    @Test
    void 주문명_확인() {
        // given
        Member member = Member.builder()
                .name("윤광오")
                .userRank(UserRank.BRONZE)
                .money(Money.ZERO)
                .password("qwer1234")
                .address(null)
                .email("qwer1234@naver.com")
                .build();

        Money money = calculator.calculateMenus(member, menuList);

        // when
        Order order = Order.createOrder(member, menuList, money);
        String orderName = menuList.get(0).getTitle() + " 외 " + menuList.size() + "개";

        // then
        assertThat(order.getName()).isEqualTo(orderName);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDER);
    }

    @Test
    void 메뉴가_비어있을_때_주문명을_만들지_못함() {
        // given
        Member member = Member.builder()
                .name("윤광오")
                .userRank(UserRank.BRONZE)
                .money(Money.ZERO)
                .password("qwer1234")
                .address(null)
                .email("qwer1234@naver.com")
                .build();

        // when
        List<Menu> emptyMenuList = new ArrayList<>();

        // then
        assertThatThrownBy(() -> Order.createOrder(member, emptyMenuList, Money.ZERO))
                .isInstanceOf(MenuException.class)
                .hasMessageContaining(EMPTY_MENUS.getMessage());
    }

    @Test
    void 주문_취소_했을때_상태_주문취소로_변경() {
        // given
        Member member = Member.builder()
                .name("윤광오")
                .userRank(UserRank.BRONZE)
                .money(Money.ZERO)
                .password("qwer1234")
                .address(null)
                .email("qwer1234@naver.com")
                .build();

        Money money = calculator.calculateMenus(member, menuList);
        Order order = Order.createOrder(member, menuList, money);

        // when
        order.cancel();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCEL);
    }

    @Test
    void 주문하고_쿠폰사용_상태_확인() {
        // given
        Member member = Member.builder()
                .name("윤광오")
                .userRank(UserRank.BRONZE)
                .money(Money.ZERO)
                .password("qwer1234")
                .address(null)
                .coupon(coupon)
                .email("qwer1234@naver.com")
                .build();

        Money money = calculator.calculateMenus(member, menuList);

        // when
        Order.createOrder(member, menuList, money);

        // then
        assertThat(member.getCoupon().getCouponUse()).isEqualTo(CouponUse.YES);
    }

    @Test
    void 다양한_할인_적용_후_주문_총_금액_확인() {
        // given
        Member member = Member.builder()
                .name("윤광오")
                .userRank(UserRank.SILVER)
                .money(Money.ZERO)
                .password("qwer1234")
                .address(null)
                .coupon(coupon)
                .email("qwer1234@naver.com")
                .build();

        Money money = calculator.calculateMenus(member, menuList);

        // when
        Order order = Order.createOrder(member, menuList, money);

        // then
        assertThat(order.fetchTotalOrderPrice()).isEqualTo("2400.0");
    }
}