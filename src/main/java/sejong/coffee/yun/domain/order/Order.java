package sejong.coffee.yun.domain.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.DateTimeEntity;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.domain.user.User;

import javax.persistence.*;
import java.util.List;

import static sejong.coffee.yun.domain.exception.ExceptionControl.EMPTY_MENUS;
import static sejong.coffee.yun.domain.order.OrderStatus.CANCEL;
import static sejong.coffee.yun.domain.order.OrderStatus.ORDER;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends DateTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "order_name")
    private String name;
    @OneToOne(fetch = FetchType.LAZY)
    private MenuList menuList;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;
    private Money orderPrice;

    private Order(String name, MenuList menuList, User user, OrderStatus status, Money orderPrice) {
        this.name = name;
        this.menuList = menuList;
        this.user = user;
        this.status = status;
        this.orderPrice = orderPrice;
    }

    public static Order createOrder(User user, MenuList menuList, Money orderPrice) {
        String orderName = makeOrderName(menuList.getMenus());

        return new Order(orderName, menuList, user, ORDER, orderPrice);
    }

    public void cancel() {
        this.status = CANCEL;
    }

    private static String makeOrderName(List<Menu> menus) {
        if(menus.size() == 0) {
            throw EMPTY_MENUS.throwException();
        } else {
            String title = menus.get(0).getTitle();

            return title + " 외" + " " + menus.size() + "개";
        }
    }
}
