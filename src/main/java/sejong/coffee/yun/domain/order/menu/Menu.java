package sejong.coffee.yun.domain.order.menu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.exception.MenuException;
import sejong.coffee.yun.domain.user.Money;

import javax.persistence.*;
import java.time.LocalDateTime;

import static sejong.coffee.yun.domain.exception.ExceptionControl.MENU_NOT_ENOUGH_QUANTITY;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@DiscriminatorColumn
public abstract class Menu {

    @Id @GeneratedValue
    private Long id;
    private String title;
    private String description;
    private Money price;
    private Nutrients nutrients;
    @Enumerated(value = EnumType.STRING)
    private MenuSize menuSize;
    @Column(name = "create_at")
    private LocalDateTime createAt;
    @Column(name = "update_at")
    private LocalDateTime updateAt;
    @Column(name = "quantity")
    private int quantity;

    protected Menu(Long id, String title, String description, Money price, Nutrients nutrients, MenuSize menuSize, LocalDateTime now, int quantity) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.nutrients = nutrients;
        this.menuSize = menuSize;
        this.createAt = now;
        this.updateAt = now;
        this.quantity = quantity;
    }

    protected Menu(String title, String description, Money price, Nutrients nutrients, MenuSize menuSize, LocalDateTime now, int quantity) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.nutrients = nutrients;
        this.menuSize = menuSize;
        this.createAt = now;
        this.updateAt = now;
        this.quantity = quantity;
    }

    public void setUpdateAt(LocalDateTime now) {
        this.updateAt = now;
    }
    public void subQuantity() {
        this.quantity--;

        if(this.quantity < 0) {
            throw new MenuException(MENU_NOT_ENOUGH_QUANTITY.getMessage());
        }
    }

    public void addQuantity() {
        this.quantity++;
    }
}
