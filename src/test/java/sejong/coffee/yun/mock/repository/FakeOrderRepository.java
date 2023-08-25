package sejong.coffee.yun.mock.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.order.OrderPayStatus;
import sejong.coffee.yun.domain.order.OrderStatus;
import sejong.coffee.yun.repository.order.OrderRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_ORDER;

@Repository
public class FakeOrderRepository implements OrderRepository {

    private final List<Order> orders = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong id = new AtomicLong(0);

    @Override
    public Order save(Order order) {
        if(order.getId() == null || order.getId() == 0L) {
            Order newOrder = Order.from(id.incrementAndGet(), order);

            orders.add(newOrder);

            return newOrder;
        }
        orders.removeIf(o -> Objects.equals(o.getId(), order.getId()));
        orders.add(order);
        return order;
    }

    @Override
    public Order findById(Long id) {
        return orders.stream()
                .filter(order -> Objects.equals(order.getId(), id))
                .findAny()
                .orElseThrow(NOT_FOUND_ORDER::notFoundException);
    }

    @Override
    public List<Order> findAll() {
        return orders;
    }

    @Override
    public void delete(Long id) {
        orders.remove(id);
    }

    @Override
    public Order findByMemberId(Long memberId) {
        return orders.stream()
                .filter(order -> Objects.equals(order.getMember().getId(), memberId))
                .findAny()
                .orElseThrow(NOT_FOUND_ORDER::notFoundException);
    }

    @Override
    public Page<Order> findAllByMemberId(Pageable pageable, Long memberId) {
        List<Order> orders = this.orders.stream()
                .filter(order -> Objects.equals(order.getMember().getId(), memberId))
                .sorted(Comparator.comparing(Order::getCreateAt))
                .toList();

        return new PageImpl<>(orders, pageable, orders.size());
    }

    @Override
    public Page<Order> findAllByMemberIdAndOrderStatus(Pageable pageable, Long memberId, OrderStatus status) {
        List<Order> orders = this.orders.stream()
                .filter(order -> Objects.equals(order.getMember().getId(), memberId))
                .filter(order -> Objects.equals(order.getStatus(), status))
                .sorted(Comparator.comparing(Order::getCreateAt))
                .toList();

        return new PageImpl<>(orders, pageable, orders.size());
    }

    @Override
    public Page<Order> findAllByMemberIdAndPayStatus(Pageable pageable, Long memberId, OrderPayStatus status) {
        List<Order> orders = this.orders.stream()
                .filter(order -> Objects.equals(order.getMember().getId(), memberId))
                .filter(order -> Objects.equals(order.getPayStatus(), status))
                .sorted(Comparator.comparing(Order::getCreateAt))
                .toList();

        return new PageImpl<>(orders, pageable, orders.size());
    }

    @Override
    public void clear() {
        orders.clear();
    }
}
