package sejong.coffee.yun.domain.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aspectj.weaver.ast.Or;
import sejong.coffee.yun.domain.DateTimeEntity;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.user.User;

import javax.persistence.*;
import java.util.List;

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
    @JoinColumn(name = "user_id")
    private User user;

    private Order(String name, MenuList menuList, User user) {
        this.name = name;
        this.menuList = menuList;
        this.user = user;
    }

    public static Order createOrder(User user, MenuList menuList) {
        String orderName = makeOrderName(menuList.getMenus());

        return new Order(orderName, menuList, user);
    }

    private static String makeOrderName(List<Menu> menus) {
        if(menus.size() == 0) {
            throw new IllegalArgumentException("장바구니가 비었습니다.");
        } else {
            String title = menus.get(0).getTitle();

            return title + " ...외" + " " + menus.size() + "개";
        }
    }
}
