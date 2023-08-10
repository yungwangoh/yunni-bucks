package sejong.coffee.yun.service.pay;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sejong.coffee.yun.controller.pay.CreatePaymentData;
import sejong.coffee.yun.domain.pay.CardPayment;
import sejong.coffee.yun.dto.CardPaymentDto;
import sejong.coffee.yun.infra.ApiService;
import sejong.coffee.yun.infra.fake.FakeApiService;
import sejong.coffee.yun.infra.fake.FakeUuidHolder;
import sejong.coffee.yun.repository.card.fake.FakeCardRepository;
import sejong.coffee.yun.repository.order.fake.FakeOrderRepository;
import sejong.coffee.yun.repository.pay.fake.FakePayRepository;
import sejong.coffee.yun.service.PayService;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static sejong.coffee.yun.domain.pay.PaymentStatus.DONE;

public class PayServiceTest extends CreatePaymentData {
    private PayService payService;

    @BeforeEach
    void init() throws IOException, InterruptedException {
        FakeApiService fakeApiService = new FakeApiService("paypaypaypay_1234");
        FakePayRepository fakePayRepository = new FakePayRepository();
        this.payService = PayService.builder()
                .payRepository(fakePayRepository)
                .uuidHolder(new FakeUuidHolder("asdfasdf"))
                .apiService(new ApiService(fakeApiService))
                .orderRepository(new FakeOrderRepository())
                .cardRepository(new FakeCardRepository())
                .build();

        CardPaymentDto.Request request = CardPaymentDto.Request.from(cardPayment);
        CardPaymentDto.Response response = fakeApiService.callExternalAPI(request);
        CardPayment.approvalPayment(cardPayment, response.paymentKey(), request.requestedAt());

        fakePayRepository.save(cardPayment);
    }

    @Test
    void findById는_DONE_상태인_결제내역_단건을_조회한다() {
        //given

        //when
        CardPayment byId = payService.findById(1L);

        //then
        assertThat(byId.getPaymentStatus()).isEqualTo(DONE);

    }

    @Test
    void getByOrderId는_결제내역_단건을_조회한다() {
        //given

        //when
        CardPayment byId = payService.getByOrderId("asdfasdf");

        //then
        assertThat(byId.getPaymentStatus()).isEqualTo(DONE);
        assertThat(byId.getOrderUuid()).isEqualTo("asdfasdf");

    }

    @Test
    void getByPaymentKey는_결제내역_단건을_조회한다() {
        //given

        //when
        CardPayment byId = payService.getByPaymentKey("asdfasdf");

        //then
        assertThat(byId.getPaymentStatus()).isEqualTo(DONE);
        assertThat(byId.getOrderUuid()).isEqualTo("paypaypaypay_1234");

    }


//
//    @Transactional
//    public CardPayment pay(CardPaymentDto.Request request) throws IOException, InterruptedException {
//
//        CardPaymentDto.Response response = apiService.callApi(request);
//        CardPayment approvalPayment = CardPayment.approvalPayment(CardPayment.fromModel(request), response.paymentKey(), response.approvedAt());
//        approvalPayment = payRepository.save(approvalPayment);
//        return approvalPayment;
//    }
//
//    @Transactional
//    public CardPaymentDto.Request initPayment(Long orderId){
//        Order order = orderRepository.findById(orderId);
//        Card card = cardRepository.findByMemberId(order.getMember().getId());
//
//        return CardPaymentDto.Request.create(card, order, uuidHolder);
//    }

}
