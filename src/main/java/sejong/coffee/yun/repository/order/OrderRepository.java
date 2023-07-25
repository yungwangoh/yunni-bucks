package sejong.coffee.yun.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import sejong.coffee.yun.domain.order.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
