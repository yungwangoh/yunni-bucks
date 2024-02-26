package sejong.coffee.yun.service.read;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.pay.CardPayment;
import sejong.coffee.yun.infra.ApiService;
import sejong.coffee.yun.infra.port.UuidHolder;
import sejong.coffee.yun.repository.card.CardRepository;
import sejong.coffee.yun.repository.order.OrderRepository;
import sejong.coffee.yun.repository.pay.PayRepository;

import java.util.List;

import static sejong.coffee.yun.domain.pay.PaymentStatus.DONE;

@Service
@RequiredArgsConstructor
@Builder
@Transactional(readOnly = true)
public class PayService {

    private final @Qualifier("tossApiServiceImpl") ApiService apiService;
    private final PayRepository payRepository;
    private final OrderRepository orderRepository;
    private final CardRepository cardRepository;
    private final UuidHolder uuidHolder;

    public CardPayment findById(long id) {
        return payRepository.findById(id);
    }

    public CardPayment getByOrderId(Long orderId) {
        return payRepository.findByOrderIdAnAndPaymentStatus(orderId, DONE);
    }

    public CardPayment getByOrderUuid(String orderUuid) {
        return payRepository.findByOrderUuidAnAndPaymentStatus(orderUuid, DONE);
    }

    public CardPayment getByPaymentKey(String paymentKey) {
        return payRepository.findByPaymentKeyAndPaymentStatus(paymentKey, DONE);
    }

    public List<CardPayment> findAll() {
        return payRepository.findAll();
    }

    public Page<CardPayment> getAllByUsernameAndPaymentStatus(Pageable pageable, String username) {
        return payRepository.findAllByUsernameAndPaymentStatus(pageable, username);
    }

    public Page<CardPayment> getAllByUsernameAndPaymentCancelStatus(Pageable pageable, String username) {
        return payRepository.findAllByUsernameAndPaymentCancelStatus(pageable, username);
    }

    public Page<CardPayment> getAllOrderByApprovedAtByDesc(Pageable pageable) {
        return payRepository.findAllOrderByApprovedAtByDesc(pageable);
    }
}
