package sejong.coffee.yun.repository.delivery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sejong.coffee.yun.domain.delivery.Delivery;

public interface DeliveryRepository {

    Delivery save(Delivery delivery);
    Page<Delivery> findByMemberId(Pageable pageable, Long memberId);
    Page<Delivery> findDeliveryStatusByMemberId(Pageable pageable, Long memberId);
}
