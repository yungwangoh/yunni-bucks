package sejong.coffee.yun.domain.order.menu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.user.Money;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@DiscriminatorValue("Bread")
public class Bread extends Menu {

    public Bread(final String title, final String description,
                 final Money price, final Nutrients nutrients,
                 final MenuSize menuSize, final LocalDateTime now) {

        super(title, description, price, nutrients, menuSize, now);
    }

    private Bread(Long id, String title, String description, Money price, Nutrients nutrients, MenuSize menuSize, LocalDateTime now) {
        super(id, title, description, price, nutrients, menuSize, now, now);
    }

    public static Bread from(Long id, Bread bread) {
        return new Bread(id, bread.getTitle(), bread.getDescription(), bread.getPrice(), bread.getNutrients(), bread.getMenuSize(), bread.getCreateAt());
    }
}
