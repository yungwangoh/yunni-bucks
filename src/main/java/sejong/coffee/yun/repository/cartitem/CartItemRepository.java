package sejong.coffee.yun.repository.cartitem;

import sejong.coffee.yun.domain.user.CartItem;
import sejong.coffee.yun.dto.cart.CartDto;

import java.util.List;

public interface CartItemRepository {

    List<CartDto.StockRecord> stockRecord(Long cartId);
    CartItem save(CartItem cartItem);
    void clear();
}
