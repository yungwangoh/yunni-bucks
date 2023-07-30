package sejong.coffee.yun.repository.order.fake;

import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.repository.order.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_ORDER;

public class FakeOrderRepository implements OrderRepository {

    private List<Order> orders = new ArrayList<>();
    private Long id = 0L;

    @Override
    public Order save(Order order) {
        Order newOrder = Order.order(++id, order);

        orders.add(newOrder);

        return newOrder;
    }

    @Override
    public Order findById(Long id) {
        return orders.stream()
                .filter(order -> Objects.equals(order.getId(), id))
                .findAny()
                .orElseThrow(NOT_FOUND_ORDER::notFoundOrderException);
    }

    @Override
    public List<Order> findAll() {
        return orders;
    }

    @Override
    public List<Order> findAllByMemberId(Long memberId) {
        return orders.stream()
                .filter(order -> Objects.equals(order.getMember().getId(), memberId))
                .collect(Collectors.toList());
    }
}
