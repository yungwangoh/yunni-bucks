package sejong.coffee.yun.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.order.menu.Beverage;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.order.menu.MenuSize;
import sejong.coffee.yun.domain.order.menu.Nutrients;
import sejong.coffee.yun.domain.user.*;
import sejong.coffee.yun.dto.order.OrderDto;
import sejong.coffee.yun.jwt.JwtProvider;
import sejong.coffee.yun.mapper.CustomMapper;
import sejong.coffee.yun.service.CartService;
import sejong.coffee.yun.service.OrderService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_CART;

@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
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

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

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
                Money.initialPrice(new BigDecimal(1000)), nutrients, MenuSize.M);

        token = "bearer accessToken";

        cart = new Cart(member, List.of(menu));

        order = Order.createOrder(member, cart.getMenuList(), menu.getPrice());

        response = new OrderDto.Response(1L, order.getName(), order.getMenuList(),
                order.getStatus(), order.getOrderPrice(), order.getPayStatus());
    }

    static List<FieldDescriptor> getResponse() {
        return List.of(
                fieldWithPath("orderId").type(JsonFieldType.NUMBER).description("주문 ID"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("주문 이름"),
                fieldWithPath("menuList").type(JsonFieldType.ARRAY).description("메뉴 목록")
                        .attributes(key("element").value("Menu 객체")),
                fieldWithPath("menuList[].id").type(JsonFieldType.NULL).description("메뉴 ID"),
                fieldWithPath("menuList[].createAt").type(JsonFieldType.NULL).description("생성일"),
                fieldWithPath("menuList[].updateAt").type(JsonFieldType.NULL).description("수정일"),
                fieldWithPath("menuList[].title").type(JsonFieldType.STRING).description("메뉴 제목"),
                fieldWithPath("menuList[].description").type(JsonFieldType.STRING).description("메뉴 설명"),
                fieldWithPath("menuList[].price.totalPrice").type(JsonFieldType.NUMBER).description("메뉴 가격"),
                fieldWithPath("menuList[].nutrients").type(JsonFieldType.OBJECT).description("영양 정보"),
                fieldWithPath("menuList[].menuSize").type(JsonFieldType.STRING).description("메뉴 크기"),
                fieldWithPath("status").type(JsonFieldType.STRING).description("주문 상태"),
                fieldWithPath("money.totalPrice").type(JsonFieldType.NUMBER).description("주문 총 가격"),
                fieldWithPath("payStatus").type(JsonFieldType.STRING).description("결제 상태")
        );
    }

    @Test
    void 주문() throws Exception {
        // given
        given(cartService.findCartByMember(anyLong())).willReturn(cart);
        given(orderService.order(anyLong(), any())).willReturn(order);
        given(customMapper.map(any(), any())).willReturn(response);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/orders")
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isCreated())
                .andDo(document("order",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        ),
                        responseFields(
                                getResponse()
                        )
                ));
    }

    @Test
    void 유저가_주문한_주문내역_찾기() throws Exception {
        // given
        given(orderService.findOrderByMemberId(anyLong())).willReturn(order);
        given(customMapper.map(any(), any())).willReturn(response);

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/orders")
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("order-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        ),
                        responseFields(
                                getResponse()
                        )
                ));
    }

    @Test
    void 주문_취소() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/orders/cancel")
                .param("orderId", "1"));

        // then
        resultActions.andExpect(status().isNoContent())
                .andDo(document("order-cancel",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("orderId").description("주문 ID")
                        )
                ));
    }

    @Test
    void 업데이트_메뉴_추가() throws Exception {
        // given
        given(orderService.updateAddMenu(anyLong(), anyLong())).willReturn(order);
        given(customMapper.map(any(), any())).willReturn(response);

        // when
        ResultActions resultActions = mockMvc.perform(patch("/api/orders/update/add")
                .header(HttpHeaders.AUTHORIZATION, token)
                .param("menuId", "1"));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("order-update-menu-add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        ),
                        requestParameters(
                                parameterWithName("menuId").description("1")
                        ),
                        responseFields(
                                getResponse()
                        )
                ));
    }

    @Test
    void 업데이트_메뉴_제거() throws Exception {
        // given
        given(orderService.updateAddMenu(anyLong(), anyLong())).willReturn(order);
        given(customMapper.map(any(), any())).willReturn(response);

        // when
        ResultActions resultActions = mockMvc.perform(patch("/api/orders/update/remove")
                .header(HttpHeaders.AUTHORIZATION, token)
                .param("menuIdx", "1"));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("order-update-menu-remove",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        ),
                        requestParameters(
                                parameterWithName("menuIdx").description("1")
                        ),
                        responseFields(
                                getResponse()
                        )
                ));
    }

    @Test
    void 빈_장바구니로_주문을_할_경우() throws Exception {
        // given
        given(cartService.findCartByMember(anyLong())).willThrow(NOT_FOUND_CART.notFoundException());

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/orders")
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isNotFound())
                .andDo(document("order-fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메세지")
                        )
                ));
    }
}