package sejong.coffee.yun.domain.delivery;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.user.Address;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReserveDelivery extends Delivery {

    @Column(name = "reserve_at")
    private LocalDate reserveAt;

    @Builder
    public ReserveDelivery(Order order, LocalDateTime createAt, LocalDateTime updateAt,
                           Address address, DeliveryStatus status, LocalDate reserveAt) {

        super(order, createAt, updateAt, address, status);
        this.reserveAt = reserveAt;
    }

    @Override
    public Delivery delivery() {
        return ReserveDelivery.builder()
                .address(getAddress())
                .createAt(getCreateAt())
                .order(getOrder())
                .status(DeliveryStatus.RESERVE)
                .updateAt(getUpdateAt())
                .reserveAt(this.reserveAt)
                .build();
    }
}
