package sejong.coffee.yun.dto.delivery;

import sejong.coffee.yun.domain.delivery.Delivery;
import sejong.coffee.yun.domain.delivery.DeliveryStatus;
import sejong.coffee.yun.domain.delivery.DeliveryType;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.user.Address;

import java.time.LocalDateTime;

public class DeliveryDto {

    public record NormalRequest(Long orderId, Address address, LocalDateTime now, DeliveryType type) {}
    public record ReserveRequest(Long orderId, Address address, LocalDateTime now, LocalDateTime reserveDate, DeliveryType type){}
    public record UpdateAddressRequest(Long deliveryId, Address address, LocalDateTime now) {}
    public record Response(Long deliveryId, Order order, LocalDateTime now,
                           Address address, DeliveryType type, DeliveryStatus status) {

        public Response(Delivery delivery) {
            this(
                    delivery.getId(),
                    delivery.getOrder(),
                    delivery.getCreateAt(),
                    delivery.getAddress(),
                    delivery.getType(),
                    delivery.getStatus()
            );
        }
    }
}
