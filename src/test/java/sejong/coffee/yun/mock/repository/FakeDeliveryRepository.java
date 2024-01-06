package sejong.coffee.yun.mock.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import sejong.coffee.yun.domain.delivery.*;
import sejong.coffee.yun.repository.delivery.DeliveryRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_DELIVERY;

@Repository
public class FakeDeliveryRepository implements DeliveryRepository {

    private final List<Delivery> deliveries = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong id = new AtomicLong(0);

    @Override
    public Delivery save(Delivery delivery) {
        if(delivery.getId() == null || delivery.getId() == 0L) {
            Delivery newDelivery;

            if (delivery instanceof NormalDelivery) {
                newDelivery = NormalDelivery.from(id.incrementAndGet(), (NormalDelivery) delivery);
            } else {
                newDelivery = ReserveDelivery.from(id.incrementAndGet(), (ReserveDelivery) delivery);
            }

            deliveries.add(newDelivery);
            return newDelivery;
        }
        deliveries.removeIf(d -> Objects.equals(d.getId(), delivery.getId()));
        deliveries.add(delivery);
        return delivery;
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
        return deliveries;
    }

    @Override
    public List<Delivery> findAllByReserveType() {
        return deliveries.stream()
                .filter(delivery -> Objects.equals(delivery.getStatus(), DeliveryStatus.READY))
                .filter(delivery -> Objects.equals(delivery.getType(), DeliveryType.RESERVE))
                .filter(delivery -> ((ReserveDelivery) delivery).getReserveAt().isBefore(LocalDateTime.now()))
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

    @Override
    public void clear() {
        deliveries.clear();
    }


}
