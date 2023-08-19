package sejong.coffee.yun.repository.menu;

import org.junit.jupiter.api.Test;
import sejong.coffee.yun.domain.order.menu.Beverage;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.order.menu.MenuSize;
import sejong.coffee.yun.domain.order.menu.Nutrients;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.mock.repository.FakeMenuRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FakeMenuRepositoryTest {

    private final MenuRepository menuRepository;

    public FakeMenuRepositoryTest() {
        this.menuRepository = new FakeMenuRepository();
    }

    @Test
    void save() {
        // given
        Nutrients nutrients = new Nutrients(80, 80, 80, 80);
        Menu beverage = new Beverage("커피", "에티오피아산 커피",
                Money.initialPrice(new BigDecimal(1000)), nutrients, MenuSize.M, LocalDateTime.now());

        // when
        Menu save = menuRepository.save(beverage);

        // then
        assertThat(save.getDescription()).isEqualTo(beverage.getDescription());
        assertThat(save.getCreateAt()).isEqualTo(beverage.getCreateAt());
        assertThat(save.getTitle()).isEqualTo(save.getTitle());
    }

    @Test
    void find() {
        // given
        Nutrients nutrients = new Nutrients(80, 80, 80, 80);
        Menu menu = new Beverage("커피", "에티오피아산 커피",
                Money.initialPrice(new BigDecimal(1000)), nutrients, MenuSize.M, LocalDateTime.now());

        Menu save = menuRepository.save(menu);

        // when
        Menu findMenu = menuRepository.findById(save.getId());

        // then
        assertThat(findMenu).isEqualTo(save);
    }
}