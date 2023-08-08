package sejong.coffee.yun.dto.cart;

import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.user.Cart;
import sejong.coffee.yun.domain.user.Member;

import java.util.List;

public class CartDto {
    public record Request() {}
    public record Response(Long cartId, Member member, List<Menu> menuList) {

        public Response(Cart cart) {
            this(cart.getId(), cart.getMember(), cart.getMenuList());
        }
    }
}
