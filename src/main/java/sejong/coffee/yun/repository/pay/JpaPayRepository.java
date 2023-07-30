package sejong.coffee.yun.repository.pay;

import org.springframework.data.jpa.repository.JpaRepository;
import sejong.coffee.yun.domain.pay.CardPayment;

public interface JpaPayRepository extends JpaRepository<CardPayment, Long> {
}
