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
                    cart.getMenuList().stream()
                            .map(MenuDto.Response::new)
                            .toList()
            );
        }
    }
}
