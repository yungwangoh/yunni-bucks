package sejong.coffee.yun.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.order.OrderPayStatus;
import sejong.coffee.yun.domain.order.OrderStatus;
import sejong.coffee.yun.repository.order.OrderRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderServiceQuery {

    private final OrderRepository orderRepository;

    public Order findOrderByMemberId(Long memberId) {
        return orderRepository.findByMemberId(memberId);
    }

    public Order findOrder(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Page<Order> findAllByMemberId(Pageable pageable, Long memberId) {
        return orderRepository.findAllByMemberId(pageable, memberId);
    }

    public Page<Order> findAllByMemberIdAndOrderStatus(Pageable pageable, Long memberId, OrderStatus status) {
        return orderRepository.findAllByMemberIdAndOrderStatus(pageable, memberId, status);
    }

    public Page<Order> findAllByMemberIdAndPayStatus(Pageable pageable, Long memberId, OrderPayStatus status) {
        return orderRepository.findAllByMemberIdAndPayStatus(pageable, memberId, status);
    }
}
