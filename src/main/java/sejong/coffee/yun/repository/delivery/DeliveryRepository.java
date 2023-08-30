package sejong.coffee.yun.repository.delivery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sejong.coffee.yun.domain.delivery.Delivery;
import sejong.coffee.yun.domain.delivery.DeliveryStatus;
import sejong.coffee.yun.domain.delivery.DeliveryType;

import java.util.List;

public interface DeliveryRepository {

    Delivery save(Delivery delivery);
    Delivery findOne(Long deliveryId);
    List<Delivery> findAll();
    List<Delivery> findAllByReserveType();
    Page<Delivery> findByMemberId(Pageable pageable, Long memberId);
    Page<Delivery> findDeliveryTypeByMemberId(Pageable pageable, Long memberId, DeliveryType type);
    Page<Delivery> findDeliveryStatusByMemberId(Pageable pageable, Long memberId, DeliveryStatus status);
    void clear();
}
