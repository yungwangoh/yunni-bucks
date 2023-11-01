package sejong.coffee.yun.domain.order.menu;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.user.Money;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@DiscriminatorValue("Beverage")
public class Beverage extends Menu {

    @Builder
    public Beverage(Long id, String title, String description, Money price, Nutrients nutrients, MenuSize menuSize, LocalDateTime now, int quantity, Long orderCount) {
        super(id, title, description, price, nutrients, menuSize, now, quantity, orderCount);
    }

    public static Beverage from(Long id, Beverage beverage) {
        return Beverage.builder()
                .id(id)
                .title(beverage.getTitle())
                .description(beverage.getDescription())
                .price(beverage.getPrice())
                .nutrients(beverage.getNutrients())
                .menuSize(beverage.getMenuSize())
                .now(beverage.getCreateAt())
                .quantity(beverage.getQuantity())
                .orderCount(beverage.getOrderCount())
                .build();
    }
}

