package sejong.coffee.yun.dto.cart;

import sejong.coffee.yun.domain.user.Cart;
import sejong.coffee.yun.dto.menu.MenuDto;

import java.util.List;

public class CartDto {
    public record Response(Long cartId, Long memberId, List<MenuDto.Response> menuList) {

        public Response(Cart cart) {
            this(
                    cart.getId(),
                    cart.getMember().getId(),
                    cart.getCartItems().stream()
                            .map(cartItem -> new MenuDto.Response(cartItem.getMenu()))
                            .toList()
            );
        }
    }
}
