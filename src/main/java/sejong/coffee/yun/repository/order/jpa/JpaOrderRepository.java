package sejong.coffee.yun.repository.order.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import sejong.coffee.yun.domain.order.Order;

import java.util.List;

public interface JpaOrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByMemberId(Long memberId);
}
