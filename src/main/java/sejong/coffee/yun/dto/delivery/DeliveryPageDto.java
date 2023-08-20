package sejong.coffee.yun.dto.delivery;

import org.springframework.data.domain.PageImpl;
import sejong.coffee.yun.domain.delivery.Delivery;

import java.util.List;

public class DeliveryPageDto {
    public record Response(int pageNum, List<DeliveryDto.Response> responses) {
        public Response(PageImpl<Delivery> deliveryPage) {
            this(
                    deliveryPage.getNumber(),
                    deliveryPage.getContent().stream().map(DeliveryDto.Response::new).toList()
            );
        }
    }
}
