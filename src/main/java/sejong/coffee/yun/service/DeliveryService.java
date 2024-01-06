package sejong.coffee.yun.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.delivery.*;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.user.Address;
import sejong.coffee.yun.repository.delivery.DeliveryRepository;
import sejong.coffee.yun.repository.order.OrderRepository;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeliveryService {

    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;

    @Transactional
    public Delivery save(Long orderId, Address address, LocalDateTime now, DeliveryType type) {

        Order order = orderRepository.findById(orderId);

        order.checkOrderPayStatus();

        NormalDelivery delivery = NormalDelivery.create(order, now, address, type, DeliveryStatus.READY);

        return deliveryRepository.save(delivery);
    }

    @Transactional
    public Delivery save(Long orderId, Address address, LocalDateTime now,
                         LocalDateTime reserveDate, DeliveryType type) {

        Order order = orderRepository.findById(orderId);

        order.checkOrderPayStatus();

        ReserveDelivery delivery = ReserveDelivery.create(order, now, address, type, DeliveryStatus.READY, reserveDate);

        return deliveryRepository.save(delivery);
    }

    @Transactional
    public Delivery cancel(Long deliveryId) {
        Delivery delivery = deliveryRepository.findOne(deliveryId);

        delivery.cancel();

        return delivery;
    }

    @Transactional
    public Long reserveDelivery(LocalDateTime reserveAt) {
        return deliveryRepository.bulkUpdate(reserveAt);
    }

    @Transactional
    public Delivery normalDelivery(Long deliveryId) {
        Delivery delivery = deliveryRepository.findOne(deliveryId);

        delivery.delivery();

        return delivery;
    }

    @Transactional
    public Delivery complete(Long deliveryId) {
        Delivery delivery = deliveryRepository.findOne(deliveryId);

        delivery.complete();

        return delivery;
    }

    @Transactional
    public Delivery updateAddress(Long deliveryId, Address address, LocalDateTime now) {
        Delivery delivery = deliveryRepository.findOne(deliveryId);

        delivery.updateAddress(address, now);

        return delivery;
    }

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
