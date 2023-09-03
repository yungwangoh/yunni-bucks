package sejong.coffee.yun.controller.pay;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.pay.PaymentStatus;
import sejong.coffee.yun.infra.ApiService;
import sejong.coffee.yun.infra.port.UuidHolder;
import sejong.coffee.yun.integration.MainIntegrationTest;
import sejong.coffee.yun.repository.card.CardRepository;
import sejong.coffee.yun.repository.cart.CartRepository;
import sejong.coffee.yun.repository.order.OrderRepository;
import sejong.coffee.yun.repository.pay.PayRepository;
import sejong.coffee.yun.repository.user.UserRepository;
import sejong.coffee.yun.service.CardService;
import sejong.coffee.yun.service.CartService;
import sejong.coffee.yun.service.OrderService;
import sejong.coffee.yun.service.PayService;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class PaymentControllerTest extends MainIntegrationTest {

    @Autowired
    public PayRepository payRepository;
    @Autowired
    public OrderRepository orderRepository;
    @Autowired
    public CardRepository cardRepository;
    @Autowired
    public UuidHolder uuidHolder;
    @Autowired
    public PayService payService;
    @Autowired
    public ApiService apiService;
    @Autowired
    public CardService cardService;
    @Autowired
    public CartService cartService;
    @Autowired
    public OrderService orderService;
    @Autowired
    public UserRepository userRepository;
    @Autowired
    public CartRepository cartRepository;

    @Nested
    @DisplayName("Pay 통합 테스트")
    @Sql(value = {"/sql/user.sql", "/sql/menu.sql", "/sql/card.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/truncate_pay.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    class PayTest {
        String token;

        @BeforeEach
        void init() throws Exception {
            token = signInModule();
        }

        @AfterEach
        void initDB() {
            payRepository.clear();
            orderRepository.clear();
            cartRepository.clear();
            cardRepository.clear();
            userRepository.clear();
        }

        @Test
        @DisplayName("유저가 카드결제를 한다")
        public void testCardPayment() throws Exception {

            // given
            cartService.createCart(1L);
            cartService.addMenu(1L, 1L);
            Order order = orderService.order(1L, LocalDateTime.now());

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/payments/card-payment/{orderId}", order.getId())
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions
                    .andExpect(status().isCreated())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.cardNumber").value("9446032384143059"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.cardExpirationMonth").value("11"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.cardExpirationYear").value("23"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.orderDto.name").value("커피빵 외 1개"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.paymentKey").isNotEmpty())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.paymentStatus").value(PaymentStatus.DONE.name()));
        }
    }

//    @Test
//    public void keyIn으로_카드결제를_한다() throws Exception {
//
//        // given
//        // when
//        ResponseEntity<CardPaymentDto.Response> result = PaymentController.builder()
//                .payService(testPayContainer.payService)
//                .customMapper(new CustomMapper())
//                .build()
//                .keyIn(1L, 1L);
//
//        // then
//        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//        assertThat(result.getBody()).isNotNull();
//        assertThat(result.getBody().paymentStatus()).isEqualTo(PaymentStatus.DONE);
//        assertThat(result.getBody().orderUuid()).isEqualTo("testUuid");
//        assertThat(result.getBody().totalAmount()).isEqualTo("3000");
//        assertThat(result.getBody().orderDto().getMember().getName()).isEqualTo("하윤");
//        assertThat(result.getBody().paymentKey()).isEqualTo("testPaymentKey");
//        assertThat(IntStream.range(0, result.getBody().cardNumber().length())
//                .filter(i -> result.getBody().cardNumber().charAt(i) != '*')
//                .allMatch(i -> result.getBody().cardNumber().charAt(i) == this.card.getNumber().charAt(i))).isTrue();
//    }
//
//    @Test
//    public void getByOrderId로_결제내역을_조회한다() throws Exception {
//
//        // given
//        PaymentController.builder()
//                .payService(testPayContainer.payService)
//                .customMapper(new CustomMapper())
//                .build()
//                .keyIn(1L, 1L);
//
//        // when
//        ResponseEntity<CardPaymentDto.Response> result = PaymentController.builder()
//                .payService(testPayContainer.payService)
//                .customMapper(new CustomMapper())
//                .build()
//                .getByOrderId("testUuid");
//
//        // then
//        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(result.getBody()).isNotNull();
//        assertThat(result.getBody().paymentStatus()).isEqualTo(PaymentStatus.DONE);
//        assertThat(result.getBody().orderUuid()).isEqualTo("testUuid");
//        assertThat(result.getBody().orderDto().getOrderPrice().getTotalPrice().toString()).isEqualTo("3000");
//        assertThat(result.getBody().orderDto().getMember().getName()).isEqualTo("하윤");
//        assertThat(result.getBody().cardExpirationYear()).isEqualTo("23");
//        assertThat(result.getBody().cardExpirationMonth()).isEqualTo("10");
//        assertThat(result.getBody().paymentKey()).isEqualTo("testPaymentKey");
//        assertThat(result.getBody().cardNumber()).isEqualTo(this.card.getNumber());
//    }
//
//    @Test
//    public void getByPaymentKey로_결제내역을_조회한다() throws Exception {
//
//        // given
//        PaymentController.builder()
//                .payService(testPayContainer.payService)
//                .customMapper(new CustomMapper())
//                .build()
//                .keyIn(1L, 1L);
//
//        // when
//        ResponseEntity<CardPaymentDto.Response> result = PaymentController.builder()
//                .payService(testPayContainer.payService)
//                .customMapper(new CustomMapper())
//                .build()
//                .getByPaymentKey("testPaymentKey");
//
//        // then
//        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(result.getBody()).isNotNull();
//        assertThat(result.getBody().paymentStatus()).isEqualTo(PaymentStatus.DONE);
//        assertThat(result.getBody().orderUuid()).isEqualTo("testUuid");
//        assertThat(result.getBody().orderDto().getOrderPrice().getTotalPrice().toString()).isEqualTo("3000");
//        assertThat(result.getBody().orderDto().getMember().getName()).isEqualTo("하윤");
//        assertThat(result.getBody().cardExpirationYear()).isEqualTo("23");
//        assertThat(result.getBody().cardExpirationMonth()).isEqualTo("10");
//        assertThat(result.getBody().paymentKey()).isEqualTo("testPaymentKey");
//        assertThat(result.getBody().cardNumber()).isEqualTo(this.card.getNumber());
//    }
//
//    @Test
//    public void cancelPaymentKey로_결제를_취소한다() throws Exception {
//
//        // given
//        PaymentController.builder()
//                .payService(testPayContainer.payService)
//                .customMapper(new CustomMapper())
//                .build()
//                .keyIn(1L, 1L);
//
//        // when
//        ResponseEntity<CardPaymentDto.Response> result = PaymentController.builder()
//                .payService(testPayContainer.payService)
//                .customMapper(new CustomMapper())
//                .build()
//                .cancelPayment("testPaymentKey", "0001");
//
//        // then
//        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(result.getBody()).isNotNull();
//        assertThat(result.getBody().paymentStatus()).isEqualTo(PaymentStatus.CANCEL);
//        assertThat(result.getBody().cancelReason().getDescription()).isEqualTo("서비스 및 상품 불만족");
//        assertThat(result.getBody().cancelReason()).isEqualTo(PaymentCancelReason.NOT_SATISFIED_SERVICE);
//    }
//
//    @Test
//    public void cancelPaymentKey로_결제취소_실패() throws Exception {
//
//        // given
//        PaymentController.builder()
//                .payService(testPayContainer.payService)
//                .customMapper(new CustomMapper())
//                .build()
//                .keyIn(1L, 1L);
//
//        // when
//        // then
//        assertThatThrownBy(() -> PaymentController.builder()
//                .payService(testPayContainer.payService)
//                .customMapper(new CustomMapper())
//                .build()
//                .cancelPayment("testPaymentKey", "0005"))
//                .isInstanceOf(ExceptionControl.NOT_MATCHED_CANCEL_STATUS.paymentException().getClass())
//                .hasMessageContaining("결제취소 사유가 올바르지 않습니다.");
//    }
//
//    @Test
//    void getAllByUsernameAndPaymentStatus로_페이징_처리를_한다() {
//
//        //given
//        IntStream.range(0, 10).forEach(i -> {
//            try {
//                PaymentController.builder()
//                        .payService(testPayContainer.payService)
//                        .customMapper(new CustomMapper())
//                        .build()
//                        .keyIn(1L, 1L);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        });
//
//        //when
//        ResponseEntity<CardPaymentPageDto.Response> result = PaymentController.builder()
//                .payService(testPayContainer.payService)
//                .customMapper(new CustomMapper())
//                .build()
//                .getAllByUsernameAndPaymentStatus(0, "하윤");
//
//        //then
//        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(Objects.requireNonNull(result.getBody()).pageNumber()).isEqualTo(0);
//    }
//
//    @Test
//    void getAllByUsernameAndPaymentCancelStatus로_페이징_처리를_한다() {
//
//        //given
//        IntStream.range(0, 10).forEach(i -> {
//            try {
//                PaymentController.builder()
//                        .payService(testPayContainer.payService)
//                        .customMapper(new CustomMapper())
//                        .build()
//                        .keyIn(1L, 1L);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        });
//
//        //when
//        ResponseEntity<CardPaymentPageDto.Response> result = PaymentController.builder()
//                .payService(testPayContainer.payService)
//                .customMapper(new CustomMapper())
//                .build()
//                .getAllByUsernameAndPaymentCancelStatus(0, "하윤");
//
//        //then
//        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(Objects.requireNonNull(result.getBody()).pageNumber()).isEqualTo(0);
//    }
//
//    @Test
//    void getAllOrderByApprovedAtByDesc로_페이징_처리를_한다() {
//
//        //given
//        IntStream.range(0, 10).forEach(i -> {
//            try {
//                PaymentController.builder()
//                        .payService(testPayContainer.payService)
//                        .customMapper(new CustomMapper())
//                        .build()
//                        .keyIn(1L, 1L);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        });
//
//        //when
//        ResponseEntity<CardPaymentPageDto.Response> result = PaymentController.builder()
//                .payService(testPayContainer.payService)
//                .customMapper(new CustomMapper())
//                .build()
//                .getAllOrderByApprovedAtByDesc(0);
//
//        //then
//        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(Objects.requireNonNull(result.getBody()).pageNumber()).isEqualTo(0);
//    }
}