package sejong.coffee.yun.domain.delivery;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.user.Address;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("R")
public class ReserveDelivery extends Delivery {

    @Column(name = "reserve_at")
    private LocalDateTime reserveAt;

    @Builder
    public ReserveDelivery(Long id, Order order, LocalDateTime now, Address address, DeliveryType type, DeliveryStatus status, LocalDateTime reserveAt) {
        super(id, order, now, address, type, status);
        this.reserveAt = reserveAt;
    }

    public static ReserveDelivery create(Order order, LocalDateTime now, Address address,
                                         DeliveryType type, DeliveryStatus status, LocalDateTime reserveAt) {

        return ReserveDelivery.builder()
                .order(order)
                .now(now)
                .address(address)
                .type(type)
                .status(status)
                .reserveAt(reserveAt)
                .build();
    }

    public static ReserveDelivery from(Long id, ReserveDelivery delivery) {
        return ReserveDelivery.builder()
                .id(id)
                .address(delivery.getAddress())
                .reserveAt(delivery.getReserveAt())
                .now(delivery.getCreateAt())
                .address(delivery.getAddress())
                .status(delivery.getStatus())
                .order(delivery.getOrder())
                .type(delivery.getType())
                .build();
    }

    @Override
    public void cancel() {
        if(getStatus() == DeliveryStatus.READY) {
            setStatus(DeliveryStatus.CANCEL);
        } else {
            throw new RuntimeException("취소가 불가능합니다.");
        }
    }

    @Override
    public void delivery() {
        if(this.reserveAt.isBefore(LocalDateTime.now())) {

            if (getStatus() == DeliveryStatus.READY) {
                setStatus(DeliveryStatus.DELIVERY);
            } else {
                throw new RuntimeException("배송이 불가능합니다.");
            }
        }
    }

    @Override
    public void complete() {
        if(getStatus() == DeliveryStatus.DELIVERY) {
            setStatus(DeliveryStatus.COMPLETE);
        } else {
            throw new RuntimeException("배송 완료가 불가능합니다.");
        }
    }
}
