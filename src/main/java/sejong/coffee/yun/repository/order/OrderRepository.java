package sejong.coffee.yun.repository.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.order.OrderPayStatus;
import sejong.coffee.yun.domain.order.OrderStatus;

import java.util.List;

public interface OrderRepository {

    Order save(Order order);
    Order findById(Long id);
    List<Order> findAll();
    void delete(Long id);
    Order findByMemberId(Long memberId);
    Page<Order> findAllByMemberId(Pageable pageable, Long memberId);
    Page<Order> findAllByMemberIdAndOrderStatus(Pageable pageable, Long memberId, OrderStatus status);
    Page<Order> findAllByMemberIdAndPayStatus(Pageable pageable, Long memberId, OrderPayStatus status);
    void clear();
}
