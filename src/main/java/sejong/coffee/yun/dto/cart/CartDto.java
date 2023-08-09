package sejong.coffee.yun.dto.cart;

import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.user.Cart;

import java.util.List;

public class CartDto {
    public record Request() {}
    public record Response(Long cartId, Long memberId, List<Menu> menuList) {

        public Response(Cart cart) {
            this(cart.getId(), cart.getMember().getId(), cart.getMenuList());
        }
    }
}
