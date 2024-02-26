package sejong.coffee.yun.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sejong.coffee.yun.domain.delivery.Delivery;
import sejong.coffee.yun.domain.delivery.DeliveryStatus;
import sejong.coffee.yun.domain.delivery.DeliveryType;
import sejong.coffee.yun.domain.user.Address;

import java.time.LocalDateTime;
import java.util.List;

public interface DeliveryService {

    default Delivery save(Long orderId, Address address, LocalDateTime now, DeliveryType type) {
        return null;
    }
    default Delivery save(Long orderId, Address address, LocalDateTime now,
                         LocalDateTime reserveDate, DeliveryType type) {
        return null;
    }
    default Delivery cancel(Long deliveryId) {
        return null;
    }
    default Long reserveDelivery(LocalDateTime reserveAt) {
        return null;
    }
    default Long reserveDeliveryInUpdate(List<Long> ids, LocalDateTime reserveAt) {
        return null;
    }
    default Delivery normalDelivery(Long deliveryId) {
       return null;
    }
    default Delivery complete(Long deliveryId) {
        return null;
    }
    default Delivery updateAddress(Long deliveryId, Address address, LocalDateTime now) {
        return null;
    }
    default Page<Delivery> findAllByMemberId(Pageable pageable, Long memberId) {
        return null;
    }
    default Page<Delivery> findDeliveryTypeAllByMemberId(Pageable pageable, Long memberId, DeliveryType type) {
        return null;
    }
    default Page<Delivery> findDeliveryStatusAllByMemberId(Pageable pageable, Long memberId, DeliveryStatus status) {
        return null;
    }
}
