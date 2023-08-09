package sejong.coffee.yun.domain.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MoneyTest {

    @Test
    void 요금_충전() {
        // given
        Money money = Money.initialPrice(new BigDecimal(10000));

        // when

        // then
        assertThat(money.getTotalPrice()).isEqualTo("10000");
    }

    @Test
    void 요금_더하기() {
        // given
        Money money = Money.initialPrice(new BigDecimal(10000));

        // when
        money.plus(Money.initialPrice(new BigDecimal(10000)));

        // then
        assertThat(money.getTotalPrice()).isEqualTo("20000");
    }

    @Test
    void 요금_빼기() {
        // given
        Money money = Money.initialPrice(new BigDecimal(10000));

        // when
        money.minus(Money.initialPrice(new BigDecimal(5000)));

        // then
        assertThat(money.getTotalPrice()).isEqualTo("5000");
    }

    @Test
    void 요금을_뺄_때_음수가_나오면_예외() {
        // given
        Money money = Money.initialPrice(new BigDecimal(10000));

        // when

        // then
        assertThatThrownBy(() -> money.minus(Money.initialPrice(new BigDecimal(50000))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잘못된 요금 계산입니다.");
    }

    @Test
    void 할인_금액() {
        // given
        Money money = Money.initialPrice(new BigDecimal(10000));

        // when
        money.discount(new BigDecimal("0.1"));

        // then
        assertThat(money.getTotalPrice()).isEqualTo("9000.0");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1.1", "-0.1"})
    void 할인률의_범위_예외(String discountRate) {
        // given
        Money money = Money.initialPrice(new BigDecimal(10000));

        // when

        // then
        assertThatThrownBy(() -> money.discount(new BigDecimal(discountRate)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("할인률은 1 이하 0이상이여야 합니다.");
    }

    @Test
    void 소수점_정수로_치환_int_discount() {
        // given
        Money money = Money.initialPrice(new BigDecimal(10000));

        // when
        money.discount(new BigDecimal("0.1"));

        money.mapBigDecimalToLong();

        // then
        assertThat(money.getTotalPrice()).isEqualTo("9000");
    }

    @Test
    void 소수점_정수로_치환_int_plus() {
        // given
        Money money = Money.initialPrice(new BigDecimal(10000));

        // when
        money.plus(Money.initialPrice(new BigDecimal(1000)));

        money.mapBigDecimalToInt();

        // then
        assertThat(money.getTotalPrice()).isEqualTo("11000");
    }

    @Test
    void 소수점_정수로_치환_long_discount() {
        // given
        Money money = Money.initialPrice(new BigDecimal(10000));

        // when
        money.discount(new BigDecimal("0.1"));

        money.mapBigDecimalToLong();

        // then
        assertThat(money.getTotalPrice()).isEqualTo("9000");
    }
}
