package sejong.coffee.yun.domain.order;

import lombok.AccessLevel;
import lombok.Builder;
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
import java.util.Objects;

import static sejong.coffee.yun.domain.exception.ExceptionControl.DO_NOT_PAID;
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

    @Builder
    public Order(Long id, String name, List<Menu> menuList, Member member, OrderStatus status, Money orderPrice, OrderPayStatus payStatus, LocalDateTime createAt, LocalDateTime updateAt) {
        this.id = id;
        this.name = name;
        this.menuList = menuList;
        this.member = member;
        this.status = status;
        this.orderPrice = orderPrice;
        this.payStatus = payStatus;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public static Order from(Long id, Order order) {
        return Order.builder()
                .id(id)
                .name(order.getName())
                .orderPrice(order.getOrderPrice())
                .createAt(order.getCreateAt())
                .menuList(order.getMenuList())
                .member(order.getMember())
                .updateAt(order.getUpdateAt())
                .status(order.getStatus())
                .payStatus(order.getPayStatus())
                .build();
    }

    public static Order createOrder(Member member, List<Menu> menuList, Money orderPrice, LocalDateTime now) {
        String orderName = makeOrderName(menuList);

        if(member.getCoupon() != null) {
            member.getCoupon().convertStatusUsedCoupon();
        }

        return Order.builder()
                .name(orderName)
                .menuList(menuList)
                .member(member)
                .status(ORDER)
                .payStatus(OrderPayStatus.NO)
                .orderPrice(orderPrice)
                .createAt(now)
                .updateAt(now)
                .build();
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

    public void checkOrderPayStatus() {
        if(!Objects.equals(this.payStatus, OrderPayStatus.YES)) {
            throw new IllegalArgumentException(DO_NOT_PAID.getMessage());
        }
    }
}
