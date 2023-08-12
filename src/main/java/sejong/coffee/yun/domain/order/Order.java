package sejong.coffee.yun.domain.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.user.CartControl;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.Money;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static sejong.coffee.yun.domain.exception.ExceptionControl.EMPTY_MENUS;
import static sejong.coffee.yun.domain.order.OrderStatus.CANCEL;
import static sejong.coffee.yun.domain.order.OrderStatus.ORDER;
import static sejong.coffee.yun.domain.user.CartControl.SIZE;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id @GeneratedValue
    private Long id;
    @Column(name = "order_name")
    private String name;
    @OneToMany
    private List<Menu> menuList;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;
    private Money orderPrice;
    @Enumerated(value = EnumType.STRING)
    private OrderPayStatus payStatus;
    @Column(name = "create_at")
    private LocalDateTime createAt;
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    private Order(String name, List<Menu> menuList, Member member,
                  OrderStatus status, Money orderPrice, OrderPayStatus payStatus,
                  LocalDateTime now) {

        this.name = name;
        this.menuList = menuList;
        this.member = member;
        this.status = status;
        this.orderPrice = orderPrice;
        this.payStatus = payStatus;
        this.createAt = now;
        this.updateAt = now;
    }

    private Order(Long id, String name, List<Menu> menuList,
                  Member member, OrderStatus status, Money orderPrice,
                  OrderPayStatus payStatus, LocalDateTime now) {

        this(name, menuList, member, status, orderPrice, payStatus, now);
        this.id = id;
    }

    public static Order from(Long id, Order order) {
        return new Order(id, order.getName(), order.getMenuList(), order.getMember(),
                order.getStatus(), order.getOrderPrice(), order.getPayStatus(),
                order.getCreateAt());
    }

    public static Order createOrder(Member member, List<Menu> menuList, Money orderPrice, LocalDateTime now) {
        String orderName = makeOrderName(menuList);

        if(member.getCoupon() != null) {
            member.getCoupon().convertStatusUsedCoupon();
        }

        return new Order(orderName, menuList, member, ORDER, orderPrice, OrderPayStatus.NO, now);
    }

    public void completePayment() {
        this.payStatus = OrderPayStatus.YES;
    }

    public void cancel() {
        this.status = CANCEL;
    }

    public BigDecimal fetchTotalOrderPrice() {
        return this.orderPrice.getTotalPrice();
    }

    public void addMenu(Menu menu) {
        if(this.menuList.size() >= CartControl.SIZE.getSize())
            throw new RuntimeException("카트는 메뉴를 " + SIZE + "개만 담을 수 있습니다.");

        this.menuList.add(menu);
    }

    public void removeMenu(int idx) {
        this.menuList.remove(idx);
    }

    private static String makeOrderName(List<Menu> menus) {
        if(menus.size() == 0) {
            throw EMPTY_MENUS.throwException();
        } else {
            String title = menus.get(0).getTitle();

            return title + " 외" + " " + menus.size() + "개";
        }
    }

    public void updatePrice(Money money) {
        this.orderPrice = money;
    }

    public void setUpdateAt(LocalDateTime now) {
        this.updateAt = now;
    }

    public String mapOrderName() {
        return this.id + "00000";
    }
}
