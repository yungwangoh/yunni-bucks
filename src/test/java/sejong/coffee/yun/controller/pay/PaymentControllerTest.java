package sejong.coffee.yun.controller.pay;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import sejong.coffee.yun.controller.pay.mock.TestPayContainer;
import sejong.coffee.yun.domain.exception.ExceptionControl;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.pay.PaymentCancelReason;
import sejong.coffee.yun.domain.pay.PaymentStatus;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.dto.card.CardDto;
import sejong.coffee.yun.dto.pay.CardPaymentDto;
import sejong.coffee.yun.mapper.CustomMapper;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentControllerTest extends CreatePaymentData {

    private TestPayContainer testPayContainer;

    @BeforeEach
    void init() {
        testPayContainer = TestPayContainer.builder()
                .uuid("testUuid")
                .paymentKey("testPaymentKey")
                .build();
        Member saveMember = testPayContainer.userRepository.save(this.member);
        testPayContainer.cardService.create(1L, new CardDto.Request(this.card.getNumber(),
                this.card.getCardPassword(), this.card.getValidThru()));
        testPayContainer.orderRepository.save(Order.createOrder(saveMember, menuList, money, LocalDateTime.now()));
    }

    @Test
    public void keyIn으로_카드결제를_한다() throws Exception {

        // given
        // when
        ResponseEntity<CardPaymentDto.Response> result = PaymentController.builder()
                .payService(testPayContainer.payService)
                .customMapper(new CustomMapper())
                .build()
                .keyIn(1L, 1L);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().paymentStatus()).isEqualTo(PaymentStatus.DONE);
        assertThat(result.getBody().orderUuid()).isEqualTo("testUuid");
        assertThat(result.getBody().totalAmount()).isEqualTo("3000");
        assertThat(result.getBody().order().getMember().getName()).isEqualTo("하윤");
        assertThat(result.getBody().paymentKey()).isEqualTo("testPaymentKey");
        assertThat(IntStream.range(0, result.getBody().cardNumber().length())
                .filter(i -> result.getBody().cardNumber().charAt(i) != '*')
                .allMatch(i -> result.getBody().cardNumber().charAt(i) == this.card.getNumber().charAt(i))).isTrue();
    }

    @Test
    public void getByOrderId로_결제내역을_조회한다() throws Exception {

        // given
        PaymentController.builder()
                .payService(testPayContainer.payService)
                .customMapper(new CustomMapper())
                .build()
                .keyIn(1L, 1L);

        // when
        ResponseEntity<CardPaymentDto.Response> result = PaymentController.builder()
                .payService(testPayContainer.payService)
                .customMapper(new CustomMapper())
                .build()
                .getByOrderId("testUuid");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().paymentStatus()).isEqualTo(PaymentStatus.DONE);
        assertThat(result.getBody().orderUuid()).isEqualTo("testUuid");
        assertThat(result.getBody().order().getOrderPrice().getTotalPrice().toString()).isEqualTo("3000");
        assertThat(result.getBody().order().getMember().getName()).isEqualTo("하윤");
        assertThat(result.getBody().cardExpirationYear()).isEqualTo("23");
        assertThat(result.getBody().cardExpirationMonth()).isEqualTo("10");
        assertThat(result.getBody().paymentKey()).isEqualTo("testPaymentKey");
        assertThat(result.getBody().cardNumber()).isEqualTo(this.card.getNumber());
    }

    @Test
    public void getByPaymentKey로_결제내역을_조회한다() throws Exception {

        // given
        PaymentController.builder()
                .payService(testPayContainer.payService)
                .customMapper(new CustomMapper())
                .build()
                .keyIn(1L, 1L);

        // when
        ResponseEntity<CardPaymentDto.Response> result = PaymentController.builder()
                .payService(testPayContainer.payService)
                .customMapper(new CustomMapper())
                .build()
                .getByPaymentKey("testPaymentKey");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().paymentStatus()).isEqualTo(PaymentStatus.DONE);
        assertThat(result.getBody().orderUuid()).isEqualTo("testUuid");
        assertThat(result.getBody().order().getOrderPrice().getTotalPrice().toString()).isEqualTo("3000");
        assertThat(result.getBody().order().getMember().getName()).isEqualTo("하윤");
        assertThat(result.getBody().cardExpirationYear()).isEqualTo("23");
        assertThat(result.getBody().cardExpirationMonth()).isEqualTo("10");
        assertThat(result.getBody().paymentKey()).isEqualTo("testPaymentKey");
        assertThat(result.getBody().cardNumber()).isEqualTo(this.card.getNumber());
    }

    @Test
    public void cancelPaymentKey로_결제를_취소한다() throws Exception {

        // given
        PaymentController.builder()
                .payService(testPayContainer.payService)
                .customMapper(new CustomMapper())
                .build()
                .keyIn(1L, 1L);

        // when
        ResponseEntity<CardPaymentDto.Response> result = PaymentController.builder()
                .payService(testPayContainer.payService)
                .customMapper(new CustomMapper())
                .build()
                .cancelPayment("testPaymentKey", "0001");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().paymentStatus()).isEqualTo(PaymentStatus.CANCEL);
        assertThat(result.getBody().cancelReason().getDescription()).isEqualTo("서비스 및 상품 불만족");
        assertThat(result.getBody().cancelReason()).isEqualTo(PaymentCancelReason.NOT_SATISFIED_SERVICE);
    }

    @Test
    public void cancelPaymentKey로_결제취소_실패() throws Exception {

        // given
        PaymentController.builder()
                .payService(testPayContainer.payService)
                .customMapper(new CustomMapper())
                .build()
                .keyIn(1L, 1L);

        // when

        // then
        assertThatThrownBy(() -> PaymentController.builder()
                .payService(testPayContainer.payService)
                .customMapper(new CustomMapper())
                .build()
                .cancelPayment("testPaymentKey", "0005"))
                .isInstanceOf(ExceptionControl.NOT_MATCHED_CANCEL_STATUS.paymentException().getClass())
                .hasMessageContaining("결제 취소사유가 올바르지 않습니다.");
    }
}