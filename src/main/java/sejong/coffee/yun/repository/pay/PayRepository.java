package sejong.coffee.yun.repository.pay;

import sejong.coffee.yun.domain.pay.CardPayment;
import sejong.coffee.yun.domain.pay.PaymentStatus;

import java.util.List;

public interface PayRepository {

    CardPayment save(CardPayment cardPayment);

    CardPayment findById(long id);

    List<CardPayment> findAll();

    CardPayment findByOrderIdAnAndPaymentStatus(String orderUuid, PaymentStatus status);
    CardPayment findByPaymentKeyAndPaymentStatus(String paymentKey, PaymentStatus paymentStatus);

}
