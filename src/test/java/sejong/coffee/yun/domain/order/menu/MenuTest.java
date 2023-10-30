package sejong.coffee.yun.domain.order.menu;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import sejong.coffee.yun.domain.exception.ExceptionControl;
import sejong.coffee.yun.domain.exception.MenuException;
import sejong.coffee.yun.domain.user.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MenuTest {

    @Test
    void 메뉴_재고_감소() {
        // given
        Nutrients nutrients = new Nutrients(80, 80, 80, 80);

        Menu menu = Beverage.builder()
                .description("에티오피아산 커피")
                .title("커피")
                .price(Money.initialPrice(new BigDecimal(1000)))
                .nutrients(nutrients)
                .menuSize(MenuSize.M)
                .now(LocalDateTime.now())
                .quantity(100)
                .build();

        // when
        menu.subQuantity();

        // then
        assertThat(menu.getQuantity()).isEqualTo(99);
    }

    @Test
    void 메뉴_재고가_0일_경우_감소하면_안된다() {
        // given
        Nutrients nutrients = new Nutrients(80, 80, 80, 80);

        Menu menu = Beverage.builder()
                .description("에티오피아산 커피")
                .title("커피")
                .price(Money.initialPrice(new BigDecimal(1000)))
                .nutrients(nutrients)
                .menuSize(MenuSize.M)
                .now(LocalDateTime.now())
                .quantity(0)
                .build();

        // when

        // then
        assertThatThrownBy(menu::subQuantity)
                .isInstanceOf(MenuException.class)
                .hasMessageContaining(ExceptionControl.MENU_NOT_ENOUGH_QUANTITY.getMessage());
    }
}