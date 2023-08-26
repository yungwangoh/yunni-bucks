package sejong.coffee.yun.dto.order;

import org.springframework.data.domain.PageImpl;
import sejong.coffee.yun.domain.order.Order;

import java.util.List;

public class  OrderPageDto {
    public record Response(int pageNum, List<OrderDto.Response> responses) {

        public Response(PageImpl<Order> orderPage) {
            this(
                    orderPage.getNumber(),
                    orderPage.getContent().stream()
                            .map(OrderDto.Response::new)
                            .toList()
            );
        }
    }
}
