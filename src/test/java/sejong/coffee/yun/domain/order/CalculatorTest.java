package sejong.coffee.yun.domain.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import sejong.coffee.yun.domain.discount.condition.RankCondition;
import sejong.coffee.yun.domain.discount.policy.PercentPolicy;
import sejong.coffee.yun.domain.order.menu.*;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.domain.user.UserRank;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static sejong.coffee.yun.domain.exception.ExceptionControl.EMPTY_MENUS;

class CalculatorTest {

    private Calculator calculator;

    public CalculatorTest() {
        this.calculator = new Calculator(new PercentPolicy(new RankCondition()));
    }

    private Menu menu1;
    private Menu menu2;
    private Menu menu3;

    @BeforeEach
    void init() {
        Nutrients nutrients = new Nutrients(80, 80, 80, 80);

        menu1 = new Beverage("커피", "에티오피아산 커피",
                Money.initialPrice(new BigDecimal(1000)), nutrients, MenuSize.M, LocalDateTime.now());
        menu2 = new Beverage("아이스티", "복숭아 아이스티",
                Money.initialPrice(new BigDecimal(1000)), nutrients, MenuSize.M, LocalDateTime.now());
        menu3 = new Bread("소라빵", "소라빵",
                Money.initialPrice(new BigDecimal(1000)), nutrients, MenuSize.M, LocalDateTime.now());
    }

    @ParameterizedTest
    @CsvSource({"BRONZE, 3000", "SILVER, 2700", "GOLD, 2550", "PLATINUM, 2400", "DIAMOND, 2100"})
    void 메뉴리스트를_계산_한다(UserRank userRank, BigDecimal bigDecimal) {
        // given
        Member member = Member.builder()
                .address(null)
                .email("qwer1234@naver.com")
                .money(Money.ZERO)
                .userRank(userRank)
                .password("qwer1234")
                .name("윤광오")
                .build();

        List<Menu> menuList = List.of(menu1, menu2, menu3);

        // when
        Money money = calculator.calculateMenus(member, menuList);

        money.mapBigDecimalToLong();

        // then
        assertThat(money.getTotalPrice()).isEqualTo(bigDecimal);
    }

    @Test
    void 메뉴_리스트가_비어있으면_계산을_수행할_수_없다() {
        // given
        Member member = Member.builder()
                .address(null)
                .email("qwer1234@naver.com")
                .money(Money.ZERO)
                .password("qwer1234")
                .name("윤광오")
                .build();

        List<Menu> menuList = new ArrayList<>();

        // then
        assertThatThrownBy(() -> calculator.calculateMenus(member, menuList))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(EMPTY_MENUS.getMessage());
    }
}