package sejong.coffee.yun.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.order.menu.Beverage;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.order.menu.MenuSize;
import sejong.coffee.yun.domain.order.menu.Nutrients;
import sejong.coffee.yun.domain.user.*;
import sejong.coffee.yun.dto.menu.MenuDto;
import sejong.coffee.yun.dto.order.OrderDto;
import sejong.coffee.yun.dto.order.OrderPageDto;
import sejong.coffee.yun.jwt.JwtProvider;
import sejong.coffee.yun.mapper.CustomMapper;
import sejong.coffee.yun.service.CartService;
import sejong.coffee.yun.service.OrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_CART;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    OrderService orderService;
    @MockBean
    CartService cartService;
    @MockBean
    CustomMapper customMapper;
    @MockBean
    JwtProvider jwtProvider;

    static Menu menu;
    static Member member;
    static String token;
    static OrderDto.Response response;
    static Order order;
    static Cart cart;
    static Page<Order> orderPage;
    static OrderPageDto.Response pageResponse;

    @BeforeAll
    static void init() {
        Address address = Address.builder()
                .city("서울시")
                .district("광진구")
                .detail("화양동")
                .zipCode("123-123")
                .build();

        member = Member.from(1L, Member.builder()
                .email("qwer1234@naver.com")
                .password("qwer1234@A")
                .name("홍길동")
                .userRank(UserRank.BRONZE)
                .money(Money.ZERO)
                .address(address)
                .build()
        );

        Nutrients nutrients = new Nutrients(80, 80, 80, 80);
        menu = new Beverage("커피", "에티오피아산 커피",
                Money.initialPrice(new BigDecimal(1000)), nutrients, MenuSize.M, LocalDateTime.now());

        token = "bearer accessToken";

        cart = new Cart(member, List.of(menu));

        order = Order.from(1L,
                Order.createOrder(member, cart.getMenuList(), menu.getPrice(), LocalDateTime.now()));

        MenuDto.Response menuResponse = new MenuDto.Response(1L, menu.getTitle(), menu.getDescription(), menu.getPrice(), menu.getNutrients(),
                menu.getMenuSize(), menu.getCreateAt(), menu.getUpdateAt());

        response = new OrderDto.Response(1L, order.getName(), List.of(menuResponse),
                order.getStatus(), order.getOrderPrice(), order.getPayStatus());

        PageRequest pr = PageRequest.of(0, 10);
        List<Order> orders = List.of(order);
        orderPage = new PageImpl<>(orders, pr, orders.size());

        pageResponse = new OrderPageDto.Response(orderPage);
    }

    @Test
    void 주문() throws Exception {
        // given
        given(cartService.findCartByMember(anyLong())).willReturn(cart);
        given(orderService.order(anyLong(), any(), any())).willReturn(order);
        given(customMapper.map(any(), any())).willReturn(response);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/orders")
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isCreated())
                .andExpect(content().json(toJson(response)));
    }

    @Test
    void 주문_취소() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/orders/cancel")
                .param("orderId", "1"));

        // then
        resultActions.andExpect(status().isNoContent());
    }

    @Test
    void 업데이트_메뉴_추가() throws Exception {
        // given
        given(orderService.updateAddMenu(anyLong(), anyLong(), any())).willReturn(order);
        given(customMapper.map(any(), any())).willReturn(response);

        // when
        ResultActions resultActions = mockMvc.perform(patch("/api/orders/update/add")
                .header(HttpHeaders.AUTHORIZATION, token)
                .param("menuId", "1"));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().json(toJson(response)));
    }

    @Test
    void 업데이트_메뉴_제거() throws Exception {
        // given
        given(orderService.updateAddMenu(anyLong(), anyLong(), any())).willReturn(order);
        given(customMapper.map(any(), any())).willReturn(response);

        // when
        ResultActions resultActions = mockMvc.perform(patch("/api/orders/update/remove")
                .header(HttpHeaders.AUTHORIZATION, token)
                .param("menuIdx", "1"));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().json(toJson(response)));
    }

    @Test
    void 빈_장바구니로_주문을_할_경우() throws Exception {
        // given
        given(cartService.findCartByMember(anyLong())).willThrow(NOT_FOUND_CART.notFoundException());

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/orders")
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void 유저가_주문_취소_상태_또는_결제된_상태일때_메뉴_수정하면_예외() throws Exception {
        // given
        given(orderService.updateAddMenu(anyLong(), anyLong(), any()))
                .willThrow(new IllegalArgumentException("주문 취소하거나 결제가 된 상태에선 수정할 수 없습니다."));

        // when
        ResultActions resultActions = mockMvc.perform(patch("/api/orders/update/add")
                .header(HttpHeaders.AUTHORIZATION, token)
                .param("menuId", "1"));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void 유저의_주문내역() throws Exception {
        // given
        given(orderService.findAllByMemberId(any(), anyLong())).willReturn(orderPage);
        given(customMapper.map(any(), any())).willReturn(pageResponse);

        // when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/orders/{pageNum}", 0)
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().json(toJson(pageResponse)));
    }

    @Test
    void 유저의_주문내역_주문상태() throws Exception {
        // given
        given(orderService.findAllByMemberIdAndOrderStatus(any(), anyLong(), any())).willReturn(orderPage);
        given(customMapper.map(any(), any())).willReturn(pageResponse);

        // when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/orders/{pageNum}/order-status", 0)
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().json(toJson(pageResponse)));
    }

    @Test
    void 유저의_주문상태_결제상태() throws Exception {
        // given
        given(orderService.findAllByMemberIdAndPayStatus(any(), anyLong(), any())).willReturn(orderPage);
        given(customMapper.map(any(), any())).willReturn(pageResponse);

        // when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/orders/{pageNum}/paid-status", 0)
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().json(toJson(pageResponse)));
    }

    private String toJson(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }
}