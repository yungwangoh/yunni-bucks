package sejong.coffee.yun.dto.order;

import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.order.OrderPayStatus;
import sejong.coffee.yun.domain.order.OrderStatus;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.user.Money;

import java.util.List;

public class OrderDto {

    public record Response(
            Long orderId,
            String name,
            List<Menu> menuList,
            OrderStatus status,
            Money money,
            OrderPayStatus payStatus
    ) {
        public Response(Order order) {
            this(order.getId(), order.getName(), order.getMenuList(),
                    order.getStatus(), order.getOrderPrice(), order.getPayStatus());
        }
    }
}
