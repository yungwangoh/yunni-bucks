package sejong.coffee.yun.repository.pay.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sejong.coffee.yun.domain.pay.CardPayment;
import sejong.coffee.yun.domain.pay.PaymentStatus;
import sejong.coffee.yun.repository.pay.PayRepository;
import sejong.coffee.yun.repository.pay.jpa.JpaPayRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PayRepositoryImpl implements PayRepository {

    private final JpaPayRepository jpaPayRepository;

    @Override
    public CardPayment save(CardPayment cardPayment) {
        return jpaPayRepository.save(cardPayment);
    }

    @Override
    public Optional<CardPayment> findById(Long id) {
        return jpaPayRepository.findById(id);
    }

    @Override
    public List<CardPayment> findAll() {
        return jpaPayRepository.findAll();
    }

    @Override
    public Optional<CardPayment> findByOrderId(String orderId) {
        return jpaPayRepository.findByOrderId(orderId);
    }

    @Override
    public Optional<CardPayment> findByPaymentKeyAndPaymentStatus(String paymentKey, PaymentStatus paymentStatus) {
        return jpaPayRepository.findByPaymentKeyAndPaymentStatus(paymentKey, paymentStatus);
    }
}
