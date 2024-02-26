package sejong.coffee.yun.service.read;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.delivery.Delivery;
import sejong.coffee.yun.domain.delivery.DeliveryStatus;
import sejong.coffee.yun.domain.delivery.DeliveryType;
import sejong.coffee.yun.repository.delivery.DeliveryRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public Page<Delivery> findAllByMemberId(Pageable pageable, Long memberId) {
        return deliveryRepository.findByMemberId(pageable, memberId);
    }

    public Page<Delivery> findDeliveryTypeAllByMemberId(Pageable pageable, Long memberId, DeliveryType type) {
        return deliveryRepository.findDeliveryTypeByMemberId(pageable, memberId, type);
    }

    public Page<Delivery> findDeliveryStatusAllByMemberId(Pageable pageable, Long memberId, DeliveryStatus status) {
        return deliveryRepository.findDeliveryStatusByMemberId(pageable, memberId, status);
    }
}
