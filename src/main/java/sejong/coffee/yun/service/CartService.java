package sejong.coffee.yun.service;

import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.user.Cart;

public interface CartService {
    default Menu getMenu(Long memberId, int idx) {return null;}
    default Cart findCartByMember(Long memberId) { return null;}
    default Cart createCart(Long memberId) {return null;}
    default Cart addMenu(Long memberId, Long menuId) {return null;}
    default void clearCart(Long cartId) {}
    default Cart removeMenu(Long memberId, int idx) {return null;}
    default void removeCart(Long memberId) {}
}
