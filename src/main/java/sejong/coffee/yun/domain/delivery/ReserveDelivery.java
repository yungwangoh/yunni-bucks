package sejong.coffee.yun.domain.delivery;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.user.Address;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("R")
public class ReserveDelivery extends Delivery {

    @Column(name = "reserve_at")
    private LocalDateTime reserveAt;

    public ReserveDelivery(LocalDateTime reserveAt) {
        this.reserveAt = reserveAt;
    }

    private ReserveDelivery(Order order, LocalDateTime now, Address address,
                            DeliveryType type, DeliveryStatus status, LocalDateTime reserveAt) {

        super(order, now, address, type, status);
        this.reserveAt = reserveAt;
    }

    public static ReserveDelivery create(Order order, LocalDateTime now, Address address,
                                         DeliveryType type, DeliveryStatus status, LocalDateTime reserveAt) {

        return new ReserveDelivery(order, now, address, type, status, reserveAt);
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
        if(this.reserveAt.isAfter(LocalDateTime.now())) {

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
