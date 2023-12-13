package sejong.coffee.yun.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.order.menu.Menu;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manu_id")
    private Menu menu;

    @Builder
    public CartItem(Long id, Cart cart, Menu menu) {
        this.id = id;
        this.cart = cart;
        this.menu = menu;
    }

    public static CartItem from(Long id, CartItem cartItem) {
        return CartItem.builder()
                .id(id)
                .cart(cartItem.getCart())
                .menu(cartItem.getMenu())
                .build();
    }

    void setCart(Cart cart) {
        this.cart = cart;
    }
}
