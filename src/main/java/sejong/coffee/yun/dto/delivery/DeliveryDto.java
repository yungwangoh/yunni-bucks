package sejong.coffee.yun.dto.delivery;

import sejong.coffee.yun.domain.delivery.*;
import sejong.coffee.yun.domain.user.Address;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class DeliveryDto {

    public record NormalRequest(
            @NotNull Long orderId,
            @NotNull Address address,
            @NotNull LocalDateTime now,
            @NotNull DeliveryType type
    ) {}
    public record ReserveRequest(
            @NotNull Long orderId,
            @NotNull Address address,
            @NotNull LocalDateTime now,
            @NotNull LocalDateTime reserveDate,
            @NotNull DeliveryType type
    ){}
    public record UpdateAddressRequest(
            @NotNull Long deliveryId,
            @NotNull Address address,
            @NotNull LocalDateTime now
    ) {}
    public record Response(Long deliveryId, String orderName, LocalDateTime createAt, LocalDateTime updateAt, Address address, DeliveryType type, DeliveryStatus status) {

        public Response(Delivery delivery) {
            this(
                    delivery.getId(),
                    delivery.getOrder().getName(),
                    delivery.getCreateAt(),
                    delivery.getUpdateAt(),
                    delivery.getAddress(),
                    delivery.getType(),
                    delivery.getStatus()
            );
        }

        public Response(NormalDelivery delivery) {
            this(
                    delivery.getId(),
                    delivery.getOrder().getName(),
                    delivery.getCreateAt(),
                    delivery.getUpdateAt(),
                    delivery.getAddress(),
                    delivery.getType(),
                    delivery.getStatus()
            );
        }

        public Response(ReserveDelivery delivery) {
            this(
                    delivery.getId(),
                    delivery.getOrder().getName(),
                    delivery.getCreateAt(),
                    delivery.getUpdateAt(),
                    delivery.getAddress(),
                    delivery.getType(),
                    delivery.getStatus()
            );
        }
    }
}
