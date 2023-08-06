package sejong.coffee.yun.repository.pay;

import sejong.coffee.yun.domain.pay.CardPayment;
import sejong.coffee.yun.domain.pay.PaymentStatus;

import java.util.List;
import java.util.Optional;

public interface PayRepository {

    CardPayment save(CardPayment cardPayment);

    Optional<CardPayment> findById(Long id);

    List<CardPayment> findAll();

    Optional<CardPayment> findByOrderId(String orderId);
    Optional<CardPayment> findByPaymentKeyAndPaymentStatus(String paymentKey, PaymentStatus paymentStatus);

}
