package sejong.coffee.yun.domain.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.order.menu.Menu;

import javax.persistence.*;
import java.util.List;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_MENU;
import static sejong.coffee.yun.domain.user.CartControl.SIZE;

@Entity
@Getter
@NoArgsConstructor
public class Cart {

    @Id @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @OneToMany
    private List<Menu> menuList;

    @Builder
    public Cart(Long id, Member member, List<Menu> menuList) {
        this.id = id;
        this.member = member;
        this.menuList = menuList;
    }

    public static Cart from(Long id, Cart cart) {
        return Cart.builder()
                .id(id)
                .member(cart.getMember())
                .menuList(cart.getMenuList())
                .build();
    }

    public void addMenu(Menu menu) {
        if(this.menuList.size() >= SIZE.getSize()) {
            throw new RuntimeException("카트는 메뉴를 " + SIZE.getSize() + "개만 담을 수 있습니다.");
        }

        this.menuList.add(menu);
    }

    public Menu getMenu(int idx) {
        try {
            return this.menuList.get(idx);
        } catch (Exception e) {
            throw NOT_FOUND_MENU.notFoundException();
        }
    }

    public void removeMenu(int idx) {
        try {
            this.menuList.remove(idx);
        } catch (Exception e) {
            throw NOT_FOUND_MENU.notFoundException();
        }
    }

    public void clearMenuList() {
        this.menuList.clear();
    }
}
