package sejong.coffee.yun.dto.order;

import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.order.OrderPayStatus;
import sejong.coffee.yun.domain.order.OrderStatus;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.dto.menu.MenuDto;

import java.util.List;

public class OrderDto {

    public record Response(
            Long orderId,
            String name,
            List<MenuDto.Response> menuList,
            OrderStatus status,
            Money money,
            OrderPayStatus payStatus
    ) {
        public Response(Order order) {
            this(
                    order.getId(),
                    order.getName(),
                    order.getCart().getCartItems().stream().map(cartItem -> new MenuDto.Response(cartItem.getMenu())).toList(),
                    order.getStatus(),
                    order.getOrderPrice(),
                    order.getPayStatus()
            );
        }
    }
}
