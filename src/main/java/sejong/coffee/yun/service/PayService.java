package sejong.coffee.yun.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.exception.ExceptionControl;
import sejong.coffee.yun.domain.pay.CardPayment;
import sejong.coffee.yun.dto.CardPaymentDto;
import sejong.coffee.yun.infra.TossAPIService;
import sejong.coffee.yun.repository.pay.PayRepository;

import java.io.IOException;
import java.util.Optional;

import static sejong.coffee.yun.domain.pay.PaymentStatus.DONE;

@Service
@RequiredArgsConstructor
public class PayService {
    private final PayRepository payRepository;
    private final TossAPIService tossAPIService;

    public Optional<CardPayment> findById(long id) {
        return payRepository.findById(id);
    }

    public CardPayment getByOrderId(String orderId) {
        return payRepository.findByOrderId(orderId)
                .orElseThrow(ExceptionControl.NOT_FOUND_PAY_DETAILS::paymentDetailsException);
    }

    public CardPayment getByPaymentKey(String paymentKey) {
        return payRepository.findByPaymentKeyAndPaymentStatus(paymentKey, DONE)
                .orElseThrow(ExceptionControl.NOT_FOUND_PAY_DETAILS::paymentDetailsException);
    }

    @Transactional
    public CardPayment create(CardPayment cardPayment) throws IOException, InterruptedException {
        CardPaymentDto.Request request = CardPaymentDto.Request.from(cardPayment);
        CardPayment result = tossAPIService.callExternalAPI(request);
        result = payRepository.save(result);
        return result;
    }

    @Transactional
    public void approvalPayment(String paymentKey, String orderId) {
        CardPayment cardPayment = payRepository.findByOrderId(orderId)
                .orElseThrow(ExceptionControl.NOT_FOUND_PAY_DETAILS::paymentDetailsException);
        if (!paymentKey.equals(cardPayment.getPaymentKey())) {
            throw ExceptionControl.NOT_MATCHED_PAYMENT_KEY.paymentDetailsException();
        }
        update(paymentKey, orderId);
        payRepository.save(cardPayment); // 결제승인
    }

    @Transactional
    public void update(String paymentKey, String orderId) {
        CardPayment findCardPayment = getByOrderId(orderId);
        if (!findCardPayment.getPaymentKey().equals(paymentKey)) {
            throw ExceptionControl.NOT_MATCHED_PAYMENT_KEY.paymentDetailsException();
        }
        findCardPayment.update();
    }
}
