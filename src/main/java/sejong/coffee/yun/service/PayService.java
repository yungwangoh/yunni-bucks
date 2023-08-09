package sejong.coffee.yun.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.exception.ExceptionControl;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.pay.CardPayment;
import sejong.coffee.yun.domain.user.Card;
import sejong.coffee.yun.dto.CardPaymentDto;
import sejong.coffee.yun.infra.TossAPIServiceImpl;
import sejong.coffee.yun.infra.port.UuidHolder;
import sejong.coffee.yun.repository.pay.PayRepository;

import java.io.IOException;
import java.util.Optional;

import static sejong.coffee.yun.domain.pay.PaymentStatus.DONE;
import static sejong.coffee.yun.dto.CardPaymentDto.Response;

@Service
@RequiredArgsConstructor
@Builder
public class PayService {

    private final PayRepository payRepository;
    private final TossAPIServiceImpl tossAPIServiceImpl;
    private final OrderService orderService;
    private final CardService cardService;
    private final UuidHolder uuidHolder;

    public Optional<CardPayment> findById(long id) {
        return payRepository.findById(id);
    }

    public CardPayment getByOrderId(String orderUuid) {
        return payRepository.findByOrderIdAnAndPaymentStatus(orderUuid, DONE)
                .orElseThrow(ExceptionControl.NOT_FOUND_PAY_DETAILS::paymentDetailsException);
    }

    public CardPayment getByPaymentKey(String paymentKey) {
        return payRepository.findByPaymentKeyAndPaymentStatus(paymentKey, DONE)
                .orElseThrow(ExceptionControl.NOT_FOUND_PAY_DETAILS::paymentDetailsException);
    }

    @Transactional
    public CardPayment pay(CardPaymentDto.Request request) throws IOException, InterruptedException {

        Response response = tossAPIServiceImpl.callExternalAPI(request);
        CardPayment approvalPayment = CardPayment.approvalPayment(CardPayment.fromModel(request), response.paymentKey(), response.approvedAt());
        approvalPayment = payRepository.save(approvalPayment);
        return approvalPayment;
    }

    @Transactional
    public CardPaymentDto.Request initPayment(Long orderId){
        Order order = orderService.findOrder(orderId);
        Card card = cardService.getByMemberId(order.getMember().getId());

        return CardPaymentDto.Request.create(card, order, uuidHolder);
    }
}
