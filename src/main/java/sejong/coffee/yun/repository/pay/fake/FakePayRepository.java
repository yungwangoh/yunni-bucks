package sejong.coffee.yun.repository.pay.fake;

import sejong.coffee.yun.domain.pay.CardPayment;
import sejong.coffee.yun.domain.pay.PaymentStatus;
import sejong.coffee.yun.repository.pay.PayRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_PAY_DETAILS;
import static sejong.coffee.yun.domain.pay.PaymentStatus.DONE;

public class FakePayRepository implements PayRepository {

    private final AtomicLong atomicGeneratedId = new AtomicLong(0);
    private final List<CardPayment> data = new ArrayList<>();

    @Override
    public CardPayment save(CardPayment cardPayment) {
        if (cardPayment.getId() == null || cardPayment.getId() == 0L) {
            CardPayment buildCardPayment = CardPayment.builder()
                    .id(atomicGeneratedId.incrementAndGet())
                    .cardNumber(cardPayment.getCardNumber())
                    .cardPassword(cardPayment.getCardPassword())
                    .customerName(cardPayment.getCustomerName())
                    .cardExpirationYear(cardPayment.getCardExpirationYear())
                    .cardExpirationMonth(cardPayment.getCardExpirationMonth())
                    .paymentKey(cardPayment.getPaymentKey())
                    .requestedAt(cardPayment.getRequestedAt())
                    .approvedAt(cardPayment.getApprovedAt())
                    .order(cardPayment.getOrder())
                    .orderUuid(cardPayment.getOrderUuid())
                    .status(cardPayment.getPaymentStatus())
                    .type(cardPayment.getPaymentType())
                    .build();
            data.add(buildCardPayment);
            return buildCardPayment;
        }
        data.removeIf(element -> Objects.equals(element.getId(), cardPayment.getId()));
        data.add(cardPayment);
        return cardPayment;
    }

    @Override
    public CardPayment findById(long id) {
        return data.stream().filter(element -> element.getId().equals(id)).findAny()
                .orElseThrow(NOT_FOUND_PAY_DETAILS::paymentDetailsException);
    }

    @Override
    public List<CardPayment> findAll() {
        return data;
    }

    @Override
    public CardPayment findByOrderIdAnAndPaymentStatus(String orderUuid, PaymentStatus status) {
        return data.stream().filter(element -> element.getOrderUuid().equals(orderUuid)
                && element.getPaymentStatus().equals(DONE)).findAny()
                .orElseThrow(NOT_FOUND_PAY_DETAILS::paymentDetailsException);
    }

    @Override
    public CardPayment findByPaymentKeyAndPaymentStatus(String paymentKey, PaymentStatus paymentStatus) {
        return data.stream().filter(element -> element.getPaymentKey().equals(paymentKey)
                && element.getPaymentStatus().equals(DONE)).findAny()
                .orElseThrow(NOT_FOUND_PAY_DETAILS::paymentDetailsException);
    }
}
