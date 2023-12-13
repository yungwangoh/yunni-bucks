package sejong.coffee.yun.domain.user;

import lombok.AccessLevel;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;

    @Builder
    public Cart(Long id, Member member, List<CartItem> cartItems) {
        this.id = id;
        this.member = member;
        this.cartItems = cartItems;
    }

    public static Cart from(Long id, Cart cart) {
        return Cart.builder()
                .id(id)
                .member(cart.getMember())
                .cartItems(cart.getCartItems())
                .build();
    }

    public void addMenu(CartItem cartItem) {
        if(this.cartItems.size() >= SIZE.getSize()) throw new RuntimeException("카트는 메뉴를 " + SIZE.getSize() + "개만 담을 수 있습니다.");

        this.cartItems.add(cartItem);
        cartItem.setCart(this);
    }

    public Menu getMenu(int idx) {
        try {
            return this.cartItems.get(idx).getMenu();
        } catch (Exception e) {
            throw NOT_FOUND_MENU.notFoundException();
        }
    }

    public void removeMenu(int idx) {
        try {
            this.cartItems.remove(idx);
        } catch (Exception e) {
            throw NOT_FOUND_MENU.notFoundException();
        }
    }

    public void clearCartItems() {
        this.cartItems.clear();
    }

    public List<Menu> convertToMenus() {
        return getCartItems().stream().map(CartItem::getMenu).toList();
    }
}
