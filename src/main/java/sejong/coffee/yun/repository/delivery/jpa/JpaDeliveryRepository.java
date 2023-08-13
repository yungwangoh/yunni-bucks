package sejong.coffee.yun.repository.delivery.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import sejong.coffee.yun.domain.delivery.Delivery;

public interface JpaDeliveryRepository extends JpaRepository<Delivery, Long> {
}
