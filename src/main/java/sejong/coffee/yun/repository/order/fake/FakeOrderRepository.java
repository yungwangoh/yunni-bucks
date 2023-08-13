package sejong.coffee.yun.repository.order.fake;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.order.OrderPayStatus;
import sejong.coffee.yun.domain.order.OrderStatus;
import sejong.coffee.yun.repository.order.OrderRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_ORDER;

public class FakeOrderRepository implements OrderRepository {

    private List<Order> orders = new ArrayList<>();
    private Long id = 0L;

    @Override
    public Order save(Order order) {
        Order newOrder = Order.from(++id, order);

        orders.add(newOrder);

        return newOrder;
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
    public Page<Order> findAllByMemberIdAndOrderStatus(Pageable pageable, Long memberId) {
        List<Order> orders = this.orders.stream()
                .filter(order -> Objects.equals(order.getMember().getId(), memberId))
                .filter(order -> Objects.equals(order.getStatus(), OrderStatus.ORDER))
                .sorted(Comparator.comparing(Order::getCreateAt))
                .toList();

        return new PageImpl<>(orders, pageable, orders.size());
    }

    @Override
    public Page<Order> findAllByMemberIdAndOrderCancelStatus(Pageable pageable, Long memberId) {
        List<Order> orders = this.orders.stream()
                .filter(order -> Objects.equals(order.getMember().getId(), memberId))
                .filter(order -> Objects.equals(order.getStatus(), OrderStatus.CANCEL))
                .sorted(Comparator.comparing(Order::getCreateAt))
                .toList();

        return new PageImpl<>(orders, pageable, orders.size());
    }

    @Override
    public Page<Order> findAllByMemberIdAndPayStatus(Pageable pageable, Long memberId) {
        List<Order> orders = this.orders.stream()
                .filter(order -> Objects.equals(order.getMember().getId(), memberId))
                .filter(order -> Objects.equals(order.getPayStatus(), OrderPayStatus.YES))
                .sorted(Comparator.comparing(Order::getCreateAt))
                .toList();

        return new PageImpl<>(orders, pageable, orders.size());
    }
}
