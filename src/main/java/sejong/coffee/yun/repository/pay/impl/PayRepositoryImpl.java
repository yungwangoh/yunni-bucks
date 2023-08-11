package sejong.coffee.yun.repository.pay.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sejong.coffee.yun.domain.pay.CardPayment;
import sejong.coffee.yun.domain.pay.PaymentStatus;
import sejong.coffee.yun.repository.pay.PayRepository;
import sejong.coffee.yun.repository.pay.jpa.JpaPayRepository;

import java.util.List;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_PAY_DETAILS;

@Repository
@RequiredArgsConstructor
public class PayRepositoryImpl implements PayRepository {

    private final JpaPayRepository jpaPayRepository;

    @Override
    public CardPayment save(CardPayment cardPayment) {
        return jpaPayRepository.save(cardPayment);
    }

    @Override
    public CardPayment findById(long id) {
        return jpaPayRepository.findById(id)
                .orElseThrow((NOT_FOUND_PAY_DETAILS::paymentDetailsException));
    }

    @Override
    public List<CardPayment> findAll() {
        return jpaPayRepository.findAll();
    }

    @Override
    public CardPayment findByOrderIdAnAndPaymentStatus(String orderUuid, PaymentStatus status) {
        return jpaPayRepository.findByOrderIdAnAndPaymentStatus(orderUuid, PaymentStatus.DONE)
                .orElseThrow(NOT_FOUND_PAY_DETAILS::paymentDetailsException);
    }

    @Override
    public CardPayment findByPaymentKeyAndPaymentStatus(String paymentKey, PaymentStatus paymentStatus) {
        return jpaPayRepository.findByPaymentKeyAndPaymentStatus(paymentKey, paymentStatus)
                .orElseThrow(NOT_FOUND_PAY_DETAILS::paymentDetailsException);
    }
}
