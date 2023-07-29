package sejong.coffee.yun.domain.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.DateTimeEntity;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.domain.user.Member;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

import static sejong.coffee.yun.domain.exception.ExceptionControl.EMPTY_MENUS;
import static sejong.coffee.yun.domain.order.OrderStatus.CANCEL;
import static sejong.coffee.yun.domain.order.OrderStatus.ORDER;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends DateTimeEntity {

    @Id @GeneratedValue
    private Long id;
    @Column(name = "order_name")
    private String name;
    @OneToOne(fetch = FetchType.LAZY)
    private MenuList menuList;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;
    private Money orderPrice;

    private Order(String name, MenuList menuList, Member member, OrderStatus status, Money orderPrice) {
        this.name = name;
        this.menuList = menuList;
        this.member = member;
        this.status = status;
        this.orderPrice = orderPrice;
    }

    private Order(Long id, String name, MenuList menuList, Member member, OrderStatus status, Money orderPrice) {
        this(name, menuList, member, status, orderPrice);
        this.id = id;
    }

    public static Order order(Long id, Order order) {
        return new Order(id, order.getName(), order.getMenuList(), order.getMember(), order.getStatus(), order.getOrderPrice());
    }

    public static Order createOrder(Member member, MenuList menuList, Money orderPrice) {
        String orderName = makeOrderName(menuList.getMenus());
        member.getCoupon().convertStatusUsedCoupon();

        return new Order(orderName, menuList, member, ORDER, orderPrice);
    }

    public void cancel() {
        this.status = CANCEL;
    }

    public BigDecimal fetchTotalOrderPrice() {
        return this.orderPrice.getTotalPrice();
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
