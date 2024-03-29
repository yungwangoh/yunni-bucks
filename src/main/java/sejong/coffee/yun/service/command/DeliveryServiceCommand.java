package sejong.coffee.yun.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.delivery.*;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.user.Address;
import sejong.coffee.yun.repository.delivery.DeliveryRepository;
import sejong.coffee.yun.repository.order.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DeliveryServiceCommand {

    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;

    public Delivery save(Long orderId, Address address, LocalDateTime now, DeliveryType type) {

        Order order = orderRepository.findById(orderId);

        order.checkOrderPayStatus();

        NormalDelivery delivery = NormalDelivery.create(order, now, address, type, DeliveryStatus.READY);

        return deliveryRepository.save(delivery);
    }

    public Delivery save(Long orderId, Address address, LocalDateTime now,
                         LocalDateTime reserveDate, DeliveryType type) {

        Order order = orderRepository.findById(orderId);

        order.checkOrderPayStatus();

        ReserveDelivery delivery = ReserveDelivery.create(order, now, address, type, DeliveryStatus.READY, reserveDate);

        return deliveryRepository.save(delivery);
    }

    public Delivery cancel(Long deliveryId) {
        Delivery delivery = deliveryRepository.findOne(deliveryId);

        delivery.cancel();

        return delivery;
    }

    public Long reserveDelivery(LocalDateTime reserveAt) {
        return deliveryRepository.bulkUpdate(reserveAt);
    }

    public Long reserveDeliveryInUpdate(List<Long> ids, LocalDateTime reserveAt) {
        return deliveryRepository.bulkInUpdate(ids, reserveAt);
    }

    public Delivery normalDelivery(Long deliveryId) {
        Delivery delivery = deliveryRepository.findOne(deliveryId);

        delivery.delivery();

        return delivery;
    }

    public Delivery complete(Long deliveryId) {
        Delivery delivery = deliveryRepository.findOne(deliveryId);

        delivery.complete();

        return delivery;
    }

    public Delivery updateAddress(Long deliveryId, Address address, LocalDateTime now) {
        Delivery delivery = deliveryRepository.findOne(deliveryId);

        delivery.updateAddress(address, now);

        return delivery;
    }
}
