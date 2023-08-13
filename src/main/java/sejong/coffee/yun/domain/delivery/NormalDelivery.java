package sejong.coffee.yun.domain.delivery;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.user.Address;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NormalDelivery extends Delivery {

    @Builder
    public NormalDelivery(Order order, LocalDateTime createAt, LocalDateTime updateAt, Address address, DeliveryStatus status) {
        super(order, createAt, updateAt, address, status);
    }

    @Override
    public Delivery delivery() {
        return NormalDelivery.builder()
                .address(getAddress())
                .createAt(getCreateAt())
                .updateAt(getUpdateAt())
                .order(getOrder())
                .status(DeliveryStatus.NORMAL)
                .build();
    }
}
