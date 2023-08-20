package sejong.coffee.yun.domain.delivery;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.user.Address;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("N")
public class NormalDelivery extends Delivery {

    private NormalDelivery(Order order, LocalDateTime now, Address address, DeliveryType type, DeliveryStatus status) {
        super(order, now, address, type, status);
    }

    private NormalDelivery(Long id, Order order, LocalDateTime createAt, LocalDateTime updateAt, Address address, DeliveryType type, DeliveryStatus status) {
        super(id, order, createAt, updateAt, address, type, status);
    }

    public static NormalDelivery create(Order order, LocalDateTime now, Address address,
                                        DeliveryType type, DeliveryStatus status) {

        return new NormalDelivery(order, now, address, type, status);
    }

    public static NormalDelivery from(Long id, NormalDelivery delivery) {
        return new NormalDelivery(id, delivery.getOrder(), delivery.getCreateAt(), delivery.getUpdateAt(), delivery.getAddress(), delivery.getType(), delivery.getStatus());
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
        if (getStatus() == DeliveryStatus.READY) {
            setStatus(DeliveryStatus.DELIVERY);
        } else {
            throw new RuntimeException("배송이 불가능합니다.");
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
