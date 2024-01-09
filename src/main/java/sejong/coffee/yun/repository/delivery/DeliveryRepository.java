package sejong.coffee.yun.repository.delivery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sejong.coffee.yun.domain.delivery.Delivery;
import sejong.coffee.yun.domain.delivery.DeliveryStatus;
import sejong.coffee.yun.domain.delivery.DeliveryType;

import java.time.LocalDateTime;
import java.util.List;

public interface DeliveryRepository {

    Delivery save(Delivery delivery);
    Delivery findOne(Long deliveryId);
    List<Delivery> findAll();
    List<Delivery> findAllByReserveType();
    Page<Delivery> findByMemberId(Pageable pageable, Long memberId);
    Page<Delivery> findDeliveryTypeByMemberId(Pageable pageable, Long memberId, DeliveryType type);
    Page<Delivery> findDeliveryStatusByMemberId(Pageable pageable, Long memberId, DeliveryStatus status);
    default Page<Long> findDeliveryIds(Pageable pageable) {return null;}
    void clear();
    default void bulkInsert(int size, List<Delivery> deliveries, String dType, LocalDateTime reserveAt) {}
    default Long bulkUpdate(LocalDateTime reserveAt) { return null; }
    default Long bulkInUpdate(List<Long> ids, LocalDateTime reserveAt) {return null;}
    default void bulkDelete() {}
    default void jdbcExecuteUpdate(List<Delivery> deliveries, LocalDateTime reserveAt) {return;}
}
