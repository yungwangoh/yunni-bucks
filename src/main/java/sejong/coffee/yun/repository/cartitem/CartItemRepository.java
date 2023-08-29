package sejong.coffee.yun.repository.cartitem;

import sejong.coffee.yun.domain.user.CartItem;

public interface CartItemRepository {

    CartItem save(CartItem cartItem);
    void clear();
}
