package sejong.coffee.yun.domain.order.menu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.DateTimeEntity;
import sejong.coffee.yun.domain.user.Money;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class Menu extends DateTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private Money price;
    private Nutrients nutrients;
    @Enumerated(value = EnumType.STRING)
    private MenuSize menuSize;

    public Menu(String title, String description, Money price, Nutrients nutrients, MenuSize menuSize) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.nutrients = nutrients;
        this.menuSize = menuSize;
    }
}
