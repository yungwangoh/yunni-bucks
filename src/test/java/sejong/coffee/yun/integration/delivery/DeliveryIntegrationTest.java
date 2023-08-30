package sejong.coffee.yun.integration.delivery;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;
import sejong.coffee.yun.domain.delivery.Delivery;
import sejong.coffee.yun.domain.delivery.DeliveryStatus;
import sejong.coffee.yun.domain.delivery.DeliveryType;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.user.Address;
import sejong.coffee.yun.domain.user.Cart;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.integration.MainIntegrationTest;
import sejong.coffee.yun.repository.cart.CartRepository;
import sejong.coffee.yun.repository.cartitem.CartItemRepository;
import sejong.coffee.yun.repository.delivery.DeliveryRepository;
import sejong.coffee.yun.repository.order.OrderRepository;
import sejong.coffee.yun.service.CartService;
import sejong.coffee.yun.service.DeliveryService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DeliveryIntegrationTest extends MainIntegrationTest {

    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private CartService cartService;
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartRepository cartRepository;

    @AfterEach
    void initDB() {
        deliveryRepository.clear();
        orderRepository.clear();
        cartRepository.clear();
        cartItemRepository.clear();
    }

    @Nested
    @DisplayName("유저가 로그인 후 장바구니에 물품을 담고 주문을 하고 배달을 진행한다.")
    @Sql(value = {"/sql/user.sql", "/sql/menu.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/truncate.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    class DeliveryTest {

        String token;
        Order saveOrder;

        @BeforeEach
        void init() throws Exception{
            token = signInModule();

            cartService.createCart(1L);

            Cart addMenu = cartService.addMenu(1L, 1L);

            Order order = Order.createOrder(addMenu, Money.initialPrice(new BigDecimal("5000")), LocalDateTime.now());
            order.completePayment();

            saveOrder = orderRepository.save(order);
        }

        @Test
        void 일반_배달_201() throws Exception {
            // given
            String s = toJson(normalRequest(saveOrder.getId()));

            // when
            ResultActions resultActions = mockMvc.perform(post(DELIVERY_API_PATH)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .content(s)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.type").value(DeliveryType.NORMAL.name()))
                    .andDo(document("normal-delivery",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            ),
                            requestFields(
                                    getDeliveryNormalRequest()
                            ),
                            responseFields(
                                    getDeliveryResponse()
                            )
                            ));
        }

        @Test
        void 예약_배달_201() throws Exception {
            // given
            String s = toJson(reserveRequest(saveOrder.getId()));

            // when
            ResultActions resultActions = mockMvc.perform(post(DELIVERY_API_PATH + "/reserve")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .content(s)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.type").value(DeliveryType.RESERVE.name()))
                    .andDo(document("reserve-delivery",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            ),
                            requestFields(
                                    getDeliveryReserveRequest()
                            ),
                            responseFields(
                                    getDeliveryResponse()
                            )
                    ));
        }

        @Test
        void 요청이_null일_경우_400() throws Exception {
            // given
            String s = toJson(normalRequest(null));

            // when
            ResultActions resultActions = mockMvc.perform(post(DELIVERY_API_PATH)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .content(s)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest())
                    .andDo(document("delivery-request-fail",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            ),
                            responseFields(
                                    getFailResponses()
                            )
                    ));
        }

        @Test
        void 배달_주소를_수정한다_200() throws Exception {
            // given
            Delivery delivery = deliveryService.save(saveOrder.getId(), saveOrder.getMember().getAddress(),
                    LocalDateTime.now(), DeliveryType.NORMAL);

            List<String> address = List.of("강릉시", "강릉군", "401 강릉로", "100-100");
            String s = toJson(updateAddressRequest(delivery.getId(),
                    new Address(address.get(0), address.get(1), address.get(2), address.get(3))));

            // when
            ResultActions resultActions = mockMvc.perform(patch(DELIVERY_API_PATH + "/address")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .content(s)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.address.city").value(address.get(0)))
                    .andExpect(jsonPath("$.address.district").value(address.get(1)))
                    .andExpect(jsonPath("$.address.detail").value(address.get(2)))
                    .andExpect(jsonPath("$.address.zipCode").value(address.get(3)))
                    .andDo(document("delivery-update-address",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            ),
                            requestFields(
                                    getUpdateAddressRequest()
                            ),
                            responseFields(
                                    getDeliveryResponse()
                            )
                        ));
        }

        @Test
        void 배달을_시작한다_200() throws Exception {
            // given
            Delivery delivery = deliveryService.save(saveOrder.getId(), saveOrder.getMember().getAddress(),
                    LocalDateTime.now(), DeliveryType.NORMAL);

            // when
            ResultActions resultActions = mockMvc.perform(get(DELIVERY_API_PATH + "/{deliveryId}", delivery.getId())
                    .header(HttpHeaders.AUTHORIZATION, token));

            // then
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(DeliveryStatus.DELIVERY.name()))
                    .andDo(document("delivery-start",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            ),
                            responseFields(
                                    getDeliveryResponse()
                            )
                    ));
        }

        @Test
        void 배달을_시작할때_READY_상태가_아닌경우_500() throws Exception {
            // given
            Delivery delivery = deliveryService.save(saveOrder.getId(), saveOrder.getMember().getAddress(),
                    LocalDateTime.now(), DeliveryType.NORMAL);

            deliveryService.normalDelivery(delivery.getId());

            // when
            ResultActions resultActions = mockMvc.perform(get(DELIVERY_API_PATH + "/{deliveryId}", delivery.getId())
                    .header(HttpHeaders.AUTHORIZATION, token));

            // then
            resultActions.andExpect(status().isInternalServerError())
                    .andDo(document("delivery-start-fail",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            ),
                            responseFields(
                                    getFailResponses()
                            )
                    ));
        }

        @Test
        void 배달을_취소한다_200() throws Exception {
            // given
            Delivery delivery = deliveryService.save(saveOrder.getId(), saveOrder.getMember().getAddress(),
                    LocalDateTime.now(), DeliveryType.NORMAL);

            // when
            ResultActions resultActions = mockMvc.perform(get(DELIVERY_API_PATH + "/{deliveryId}/cancel", delivery.getId())
                    .header(HttpHeaders.AUTHORIZATION, token));

            // then
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(DeliveryStatus.CANCEL.name()))
                    .andDo(document("delivery-cancel",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            ),
                            responseFields(
                                    getDeliveryResponse()
                            )
                    ));
        }

        @Test
        void 배달을_취소할때_READY_상태가_아닌경우_500() throws Exception {
            // given
            Delivery delivery = deliveryService.save(saveOrder.getId(), saveOrder.getMember().getAddress(),
                    LocalDateTime.now(), DeliveryType.NORMAL);

            deliveryService.normalDelivery(delivery.getId());

            // when
            ResultActions resultActions = mockMvc.perform(get(DELIVERY_API_PATH + "/{delivery}/cancel", delivery.getId())
                    .header(HttpHeaders.AUTHORIZATION, token));

            // then
            resultActions.andExpect(status().isInternalServerError())
                    .andDo(document("delivery-cancel-fail",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            ),
                            responseFields(
                                    getFailResponses()
                            )
                    ));
        }

        @Test
        void 배달을_완료한다_200() throws Exception {
            // given
            Delivery delivery = deliveryService.save(saveOrder.getId(), saveOrder.getMember().getAddress(),
                    LocalDateTime.now(), DeliveryType.NORMAL);

            deliveryService.normalDelivery(delivery.getId());

            // when
            ResultActions resultActions = mockMvc.perform(get(DELIVERY_API_PATH + "/{deliveryId}/complete", delivery.getId())
                    .header(HttpHeaders.AUTHORIZATION, token));

            // then
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(DeliveryStatus.COMPLETE.name()))
                    .andDo(document("delivery-complete",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            ),
                            responseFields(
                                    getDeliveryResponse()
                            )
                    ));
        }

        @Test
        void 배달을_완료할떄_DELIVERY_상태가_아닌경우_500() throws Exception {
            // given
            Delivery delivery = deliveryService.save(saveOrder.getId(), saveOrder.getMember().getAddress(),
                    LocalDateTime.now(), DeliveryType.NORMAL);

            deliveryService.cancel(delivery.getId());

            // when
            ResultActions resultActions = mockMvc.perform(get(DELIVERY_API_PATH + "/{deliveryId}/complete", delivery.getId())
                    .header(HttpHeaders.AUTHORIZATION, token));

            // then
            resultActions.andExpect(status().isInternalServerError())
                    .andDo(document("delivery-complete-fail",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            ),
                            responseFields(
                                    getFailResponses()
                            )
                    ));
        }

        @Test
        void 배달_내역_조회_200() throws Exception {
            // given

            // when
            ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get(DELIVERY_API_PATH + "/page/{pageNum}", 0)
                    .header(HttpHeaders.AUTHORIZATION, token));

            // then
            resultActions.andExpect(status().isOk())
                    .andDo(document("delivery-page-find",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            ),
                            pathParameters(
                                    parameterWithName("pageNum").description("페이지 번호")
                            ),
                            responseFields(
                                    getDeliveryPageResponse()
                            )
                    ));
        }

        @ParameterizedTest
        @ValueSource(strings = {"READY", "DELIVERY", "CANCEL", "COMPLETE"})
        void 배달_상태기준_내역_조회_200(DeliveryStatus status) throws Exception {
            // given

            // when
            ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get(DELIVERY_API_PATH + "/page/{pageNum}/delivery-status", 0)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .param("status", status.name()));

            // then
            resultActions.andExpect(status().isOk())
                    .andDo(document("delivery-page-find-status",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            ),
                            pathParameters(
                                    parameterWithName("pageNum").description("페이지 번호")
                            ),
                            responseFields(
                                    getDeliveryPageResponse()
                            )
                    ));
        }

        @ParameterizedTest
        @ValueSource(strings = {"RESERVE", "NORMAL"})
        void 배달_타입기준_내역_조회_200(DeliveryType type) throws Exception {
            // given

            // when
            ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get(DELIVERY_API_PATH + "/page/{pageNum}/delivery-type", 0)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .param("type", type.name()));

            // then
            resultActions.andExpect(status().isOk())
                    .andDo(document("delivery-page-find-type",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            ),
                            pathParameters(
                                    parameterWithName("pageNum").description("페이지 번호")
                            ),
                            responseFields(
                                    getDeliveryPageResponse()
                            )
                    ));
        }
    }
}
