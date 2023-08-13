package sejong.coffee.yun.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.pay.CardPayment;
import sejong.coffee.yun.domain.user.Card;
import sejong.coffee.yun.dto.pay.CardPaymentDto;
import sejong.coffee.yun.infra.ApiService;
import sejong.coffee.yun.infra.port.UuidHolder;
import sejong.coffee.yun.repository.card.CardRepository;
import sejong.coffee.yun.repository.order.OrderRepository;
import sejong.coffee.yun.repository.pay.PayRepository;

import java.io.IOException;

import static sejong.coffee.yun.domain.pay.PaymentStatus.DONE;
import static sejong.coffee.yun.dto.pay.CardPaymentDto.Response;

@Service
@RequiredArgsConstructor
@Builder
public class PayService {

    private final ApiService apiService;
    private final PayRepository payRepository;
    private final OrderRepository orderRepository;
    private final CardRepository cardRepository;
    private final UuidHolder uuidHolder;

    public CardPayment findById(long id) {
        return payRepository.findById(id);
    }

    public CardPayment getByOrderId(String orderUuid) {
        return payRepository.findByOrderIdAnAndPaymentStatus(orderUuid, DONE);
    }

    public CardPayment getByPaymentKey(String paymentKey) {
        return payRepository.findByPaymentKeyAndPaymentStatus(paymentKey, DONE);
    }

    @Transactional
    public CardPayment pay(CardPaymentDto.Request request) throws IOException, InterruptedException {

        Response response = apiService.callApi(request);
        CardPayment approvalPayment = CardPayment.approvalPayment(CardPayment.fromModel(request), response.paymentKey(), response.approvedAt());
        approvalPayment = payRepository.save(approvalPayment);
        return approvalPayment;
    }

    @Transactional
    public CardPaymentDto.Request initPayment(Long orderId, Long memberId){
        Order order = orderRepository.findById(orderId);
        Card card = cardRepository.findByMemberId(memberId);

        return CardPaymentDto.Request.create(card, order, uuidHolder);
    }
}
