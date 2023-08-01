package sejong.coffee.yun.dto.order;

import sejong.coffee.yun.domain.order.MenuList;
import sejong.coffee.yun.domain.order.OrderPayStatus;
import sejong.coffee.yun.domain.order.OrderStatus;
import sejong.coffee.yun.domain.user.Money;

public class OrderDto {
    public static class Order {
        public record Response(
                Long orderId,
                String name,
                MenuList menuList,
                OrderStatus status,
                Money money,
                OrderPayStatus payStatus
        ) {}
    }
}
