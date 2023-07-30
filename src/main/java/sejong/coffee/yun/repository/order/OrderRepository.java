package sejong.coffee.yun.repository.order;

import sejong.coffee.yun.domain.order.Order;

import java.util.List;

public interface OrderRepository {

    Order save(Order order);
    Order findById(Long id);
    List<Order> findAll();
    List<Order> findAllByMemberId(Long memberId);
}
