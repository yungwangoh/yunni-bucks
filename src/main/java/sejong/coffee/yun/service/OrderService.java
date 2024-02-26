package sejong.coffee.yun.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.order.OrderPayStatus;
import sejong.coffee.yun.domain.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    default Order order(Long memberId, LocalDateTime now) { return null; }
    default void cancel(Long orderId) {}
    default Order findOrderByMemberId(Long memberId) {
        return null;
    }
    default Order findOrder(Long orderId) {
        return null;
    }
    default List<Order> findAll() {
        return null;
    }
    default Page<Order> findAllByMemberId(Pageable pageable, Long memberId) {
        return null;
    }
    default Page<Order> findAllByMemberIdAndOrderStatus(Pageable pageable, Long memberId, OrderStatus status) {
        return null;
    }
    default Page<Order> findAllByMemberIdAndPayStatus(Pageable pageable, Long memberId, OrderPayStatus status) {
        return null;
    }
}
