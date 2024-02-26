package sejong.coffee.yun.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import sejong.coffee.yun.domain.delivery.*;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.order.menu.Beverage;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.order.menu.MenuSize;
import sejong.coffee.yun.domain.order.menu.Nutrients;
import sejong.coffee.yun.domain.user.*;
import sejong.coffee.yun.dto.delivery.DeliveryDto;
import sejong.coffee.yun.dto.delivery.DeliveryPageDto;
import sejong.coffee.yun.jwt.JwtProvider;
import sejong.coffee.yun.mapper.CustomMapper;
import sejong.coffee.yun.service.command.DeliveryServiceCommand;
import sejong.coffee.yun.service.query.DeliveryServiceQuery;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sejong.coffee.yun.domain.exception.ExceptionControl.*;

@Disabled
@WebMvcTest(DeliveryController.class)
class DeliveryControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    DeliveryServiceCommand deliveryServiceCommand;
    @MockBean
    DeliveryServiceQuery deliveryServiceQuery;
    @MockBean
    JwtProvider jwtProvider;
    @MockBean
    CustomMapper customMapper;

    static String token;
    static Order order;
    static Menu menu;
    static List<CartItem> menuList = new ArrayList<>();
    static Member member;
    static Delivery reserveDelivery;
    static Delivery normalDelivery;
    static Page<Delivery> deliveryPage;
    static DeliveryDto.Response response;
    static DeliveryPageDto.Response pageResponse;

    @BeforeAll
    static void init() {
        member = Member.builder()
                .address(new Address("서울시", "광진구", "화양동", "123-432"))
                .userRank(UserRank.BRONZE)
                .name("홍길동")
                .password("qwer1234@A")
                .money(Money.ZERO)
                .email("qwer123@naver.com")
                .orderCount(0)
                .build();

        Nutrients nutrients = new Nutrients(80, 80, 80, 80);

        menu = Beverage.builder()
                .description("에티오피아산 커피")
                .title("커피")
                .price(Money.initialPrice(new BigDecimal(1000)))
                .nutrients(nutrients)
                .menuSize(MenuSize.M)
                .now(LocalDateTime.now())
                .build();

        menuList.add(CartItem.builder().menu(menu).build());

        Cart cart = Cart.builder()
                .id(1L)
                .member(member)
                .cartItems(menuList)
                .build();

        order = Order.createOrder(member, cart, Money.initialPrice(new BigDecimal("10000")), LocalDateTime.now());

        reserveDelivery = ReserveDelivery.create(
                order,
                LocalDateTime.now(),
                member.getAddress(),
                DeliveryType.RESERVE,
                DeliveryStatus.READY,
                LocalDateTime.now()
        );

        normalDelivery = NormalDelivery.create(
                order,
                LocalDateTime.now(),
                member.getAddress(),
                DeliveryType.RESERVE,
                DeliveryStatus.READY
        );

        token = "bearer accessToken";

        PageRequest pr = PageRequest.of(0, 10);
        List<Delivery> deliveries = List.of(reserveDelivery, normalDelivery);
        deliveryPage = new PageImpl<>(deliveries, pr, deliveries.size());

        response = new DeliveryDto.Response(normalDelivery);
        pageResponse = new DeliveryPageDto.Response(1, List.of(response));
    }

    @Test
    void 일반_배달_등록_API() throws Exception {
        // given
        given(deliveryServiceCommand.save(anyLong(), any(), any(), any())).willReturn(normalDelivery);
        given(customMapper.map(any(), any())).willReturn(response);

        String toJson = toJson(new DeliveryDto.NormalRequest(1L, member.getAddress(), LocalDateTime.now(), DeliveryType.NORMAL));

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/deliveries")
                .content(toJson)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isCreated())
                .andExpect(content().json(toJson(response)));
    }

    @Test
    void 예약_배달_등록_API() throws Exception {
        // given
        given(deliveryServiceCommand.save(anyLong(), any(), any(), any())).willReturn(reserveDelivery);
        given(customMapper.map(any(), any())).willReturn(response);

        String toJson = toJson(new DeliveryDto.ReserveRequest(1L, member.getAddress(), LocalDateTime.now(), LocalDateTime.now(), DeliveryType.RESERVE));

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/deliveries/reserve")
                .content(toJson)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isCreated())
                .andExpect(content().json(toJson(response)));
    }

    @Test
    void 결제가_되어있지_않고_배달_등록을_할_때_400() throws Exception {
        // given
        given(deliveryServiceCommand.save(anyLong(), any(), any(), any()))
                .willThrow(new IllegalArgumentException(DO_NOT_PAID.getMessage()));

        String toJson = toJson(new DeliveryDto.NormalRequest(1L, member.getAddress(), LocalDateTime.now(), DeliveryType.NORMAL));

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/deliveries")
                .content(toJson)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));


        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void 배달_주소_변경_API() throws Exception {
        // given
        given(deliveryServiceCommand.updateAddress(anyLong(), any(), any())).willReturn(normalDelivery);
        given(customMapper.map(any(), any())).willReturn(response);

        String toJson = toJson(new DeliveryDto.UpdateAddressRequest(1L, member.getAddress(), LocalDateTime.now()));

        // when
        ResultActions resultActions = mockMvc.perform(patch("/api/deliveries/address")
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(toJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().json(toJson(response)));
    }

    @Test
    void 배달_주소_변경_배달_내역을_찾을_수_없을_때_404() throws Exception {
        // given
        given(deliveryServiceCommand.updateAddress(anyLong(), any(), any())).willThrow(NOT_FOUND_DELIVERY.notFoundException());

        String toJson = toJson(new DeliveryDto.UpdateAddressRequest(1L, member.getAddress(), LocalDateTime.now()));

        // when
        ResultActions resultActions = mockMvc.perform(patch("/api/deliveries/address")
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(toJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void 배달_API() throws Exception {
        // given
        given(deliveryServiceCommand.normalDelivery(anyLong())).willReturn(normalDelivery);
        given(customMapper.map(any(), any())).willReturn(response);

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/deliveries/{deliveryId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().json(toJson(response)));
    }

    @Test
    void 배달_API_예외_준비_상태가_아닐경우_500() throws Exception {
        // given
        given(deliveryServiceCommand.normalDelivery(anyLong())).willThrow(new RuntimeException(DELIVERY_EXCEPTION.getMessage()));

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/deliveries/{deliveryId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isInternalServerError());
    }

    @Test
    void 배달_취소_API() throws Exception {
        // given
        given(deliveryServiceCommand.cancel(anyLong())).willReturn(normalDelivery);
        given(customMapper.map(any(), any())).willReturn(response);

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/deliveries/{deliveryId}/cancel", 1L)
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().json(toJson(response)));
    }

    @Test
    void 배달_취소_API_예외_준비_상태가_아닐경우_500() throws Exception {
        // given
        given(deliveryServiceCommand.cancel(anyLong())).willThrow(new RuntimeException(DELIVERY_CANCEL_EXCEPTION.getMessage()));

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/deliveries/{deliveryId}/cancel", 1L)
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isInternalServerError());
    }

    @Test
    void 배달_완료_API() throws Exception {
        // given
        given(deliveryServiceCommand.complete(anyLong())).willReturn(normalDelivery);
        given(customMapper.map(any(), any())).willReturn(response);

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/deliveries/{deliveryId}/complete", 1L)
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().json(toJson(response)));
    }

    @Test
    void 배달_완료_API_예외_배송_상태가_아닐경우_500() throws Exception {
        // given
        given(deliveryServiceCommand.complete(anyLong())).willThrow(new RuntimeException(DELIVERY_COMPLETE_EXCEPTION.getMessage()));

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/deliveries/{deliveryId}/complete", 1L)
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isInternalServerError());
    }

    @Test
    void 유저의_배달_내역() throws Exception {
        // given
        given(deliveryServiceQuery.findAllByMemberId(any(), anyLong())).willReturn(deliveryPage);
        given(customMapper.map(any(), any())).willReturn(pageResponse);

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/deliveries/page/{pageNum}", 1)
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().json(toJson(pageResponse)));
    }

    @Test
    void 유저의_배달_내역_조건_타입() throws Exception {
        // given
        given(deliveryServiceQuery.findDeliveryTypeAllByMemberId(any(), anyLong(), any())).willReturn(deliveryPage);
        given(customMapper.map(any(), any())).willReturn(pageResponse);

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/deliveries/page/{pageNum}/delivery-type", 1)
                .param("type", DeliveryType.NORMAL.name())
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().json(toJson(pageResponse)));
    }

    @Test
    void 유저의_배달_내역_조건_상태() throws Exception {
        // given
        given(deliveryServiceQuery.findDeliveryStatusAllByMemberId(any(), anyLong(), any())).willReturn(deliveryPage);
        given(customMapper.map(any(), any())).willReturn(pageResponse);

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/deliveries/page/{pageNum}/delivery-status", 1)
                .param("status", DeliveryStatus.DELIVERY.name())
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().json(toJson(pageResponse)));
    }

    private String toJson(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }
}
