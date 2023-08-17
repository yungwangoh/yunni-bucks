package sejong.coffee.yun.mock.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import sejong.coffee.yun.domain.delivery.*;
import sejong.coffee.yun.repository.delivery.DeliveryRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_DELIVERY;


public class FakeDeliveryRepository implements DeliveryRepository {

    private final List<Delivery> deliveries = new ArrayList<>();
    private Long id = 0L;

    @Override
    public Delivery save(Delivery delivery) {
        Delivery newDelivery;

        if(delivery instanceof NormalDelivery) {
            newDelivery = NormalDelivery.from(++id, (NormalDelivery) delivery);
        } else {
            newDelivery = ReserveDelivery.from(++id, (ReserveDelivery) delivery);
        }

        deliveries.add(newDelivery);

        return newDelivery;
    }

    @Override
    public Delivery findOne(Long deliveryId) {
        return deliveries.stream()
                .filter(delivery -> Objects.equals(delivery.getId(), deliveryId))
                .findAny()
                .orElseThrow(NOT_FOUND_DELIVERY::notFoundException);
    }

    @Override
    public List<Delivery> findAll() {
        return deliveries.stream()
                .filter(delivery -> Objects.equals(delivery.getStatus(), DeliveryStatus.READY))
                .filter(delivery -> delivery instanceof ReserveDelivery)
                .sorted(Comparator.comparing(delivery -> ((ReserveDelivery) delivery).getReserveAt())
                        .thenComparing(delivery -> ((ReserveDelivery) delivery).getCreateAt()))
                .toList();
    }

    @Override
    public Page<Delivery> findByMemberId(Pageable pageable, Long memberId) {
        List<Delivery> list = deliveries.stream()
                .filter(delivery -> Objects.equals(delivery.getOrder().getMember().getId(), memberId))
                .sorted(Comparator.comparing(Delivery::getCreateAt).reversed())
                .toList();

        return new PageImpl<>(list, pageable, list.size());
    }

    @Override
    public Page<Delivery> findDeliveryTypeByMemberId(Pageable pageable, Long memberId, DeliveryType type) {
        List<Delivery> list = deliveries.stream()
                .filter(delivery -> Objects.equals(delivery.getOrder().getMember().getId(), memberId))
                .filter(delivery -> Objects.equals(delivery.getType(), type))
                .sorted(Comparator.comparing(Delivery::getCreateAt).reversed())
                .toList();

        return new PageImpl<>(list, pageable, list.size());
    }

    @Override
    public Page<Delivery> findDeliveryStatusByMemberId(Pageable pageable, Long memberId, DeliveryStatus status) {
        List<Delivery> list = deliveries.stream()
                .filter(delivery -> Objects.equals(delivery.getOrder().getMember().getId(), memberId))
                .filter(delivery -> Objects.equals(delivery.getStatus(), status))
                .sorted(Comparator.comparing(Delivery::getCreateAt).reversed())
                .toList();

        return new PageImpl<>(list, pageable, list.size());
    }
}
