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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sejong.coffee.yun.domain.order.menu.Beverage;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.order.menu.MenuSize;
import sejong.coffee.yun.domain.order.menu.Nutrients;
import sejong.coffee.yun.domain.user.*;
import sejong.coffee.yun.dto.cart.CartDto;
import sejong.coffee.yun.jwt.JwtProvider;
import sejong.coffee.yun.mapper.CustomMapper;
import sejong.coffee.yun.service.CartService;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_CART;
import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_MENU;
import static sejong.coffee.yun.domain.user.CartControl.SIZE;

@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    CartService cartService;
    @MockBean
    JwtProvider jwtProvider;
    @MockBean
    CustomMapper customMapper;

    private static Cart cart;
    private static String token;
    private static Member member;
    private static CartDto.Response response;
    private static Menu menu;

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


        cart = new Cart(member, new ArrayList<>());
        token = "bearer accessToken";
        response = new CartDto.Response(cart);
    }

    @Test
    void 카트_생성() throws Exception {
        // given
        given(cartService.createCart(anyLong())).willReturn(cart);
        given(customMapper.map(any(), any())).willReturn(response);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/carts")
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isCreated())
                .andDo(document("cart-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        ),
                        responseFields(
                                fieldWithPath("cartId").description("카트 ID"),
                                fieldWithPath("memberId").description("카트에 속한 회원 정보 ID"),
                                fieldWithPath("menuList").type(JsonFieldType.ARRAY).description("카트에 담긴 메뉴 리스트")
                        )
                ));
    }

    @Test
    void 카트_조회() throws Exception {
        // given
        given(cartService.findCartByMember(anyLong())).willReturn(cart);
        given(customMapper.map(any(), any())).willReturn(response);

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/carts")
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("cart-find",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        ),
                        responseFields(
                                fieldWithPath("cartId").description("카트 ID"),
                                fieldWithPath("memberId").description("카트에 속한 회원 정보 ID"),
                                fieldWithPath("menuList").type(JsonFieldType.ARRAY).description("카트에 담긴 메뉴 리스트")
                        )
        ));
    }

    @Test
    void 카트_삭제() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(delete("/api/carts")
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isNoContent())
                .andDo(document("cart-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        )
                ));
    }

    @Test
    void 메뉴_추가() throws Exception {
        // given
        given(cartService.addMenu(anyLong(), anyLong())).willReturn(cart);
        given(customMapper.map(any(), any())).willReturn(response);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/carts/menu")
                .header(HttpHeaders.AUTHORIZATION, token)
                .param("menuId", "1"));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("menu-add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        ),
                        requestParameters(
                                parameterWithName("menuId").description("메뉴 ID")
                        ),
                        responseFields(
                                fieldWithPath("cartId").description("카트 ID"),
                                fieldWithPath("memberId").description("카트에 속한 회원 정보 ID"),
                                fieldWithPath("menuList").type(JsonFieldType.ARRAY).description("카트에 담긴 메뉴 리스트")
                        )
                ));
    }

    @Test
    void 메뉴_삭제() throws Exception {
        // given
        given(cartService.removeMenu(anyLong(), anyInt())).willReturn(cart);
        given(customMapper.map(any(), any())).willReturn(response);

        // when
        ResultActions resultActions = mockMvc.perform(delete("/api/carts/menu")
                .header(HttpHeaders.AUTHORIZATION, token)
                .param("menuIdx", "1"));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("menu-remove",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        ),
                        requestParameters(
                                parameterWithName("menuIdx").description("카트 INDEX (리스트 번호)")
                        )
                ));
    }

    @Test
    void 카트에서_메뉴_꺼내기() throws Exception {
        // given
        given(cartService.getMenu(anyLong(), anyInt())).willReturn(menu);

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/carts/menu")
                .param("menuIdx", "1")
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("menu-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        ),
                        requestParameters(
                                parameterWithName("menuIdx").description("카트 INDEX (리스트 번호)")
                        ),
                        responseFields(
                                fieldWithPath("id").description("메뉴 ID"),
                                fieldWithPath("title").description("메뉴 제목"),
                                fieldWithPath("description").description("메뉴 설명"),
                                fieldWithPath("price.totalPrice").description("메뉴 가격 정보"),
                                fieldWithPath("nutrients").description("메뉴 영양 정보"),
                                fieldWithPath("menuSize").description("메뉴 크기"),
                                fieldWithPath("createAt").description("생성일"),
                                fieldWithPath("updateAt").description("수정일")
                        )
                ));
    }

    @Test
    void 카트가_생성되지_않았는데_메뉴를_추가_할_경우() throws Exception {
        // given
        given(cartService.addMenu(anyLong(), anyLong())).willThrow(NOT_FOUND_CART.notFoundException());

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/carts/menu")
                .header(HttpHeaders.AUTHORIZATION, token)
                .param("menuId", "1"));

        // then
        resultActions.andExpect(status().isNotFound())
                .andDo(document("menu-add-fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        ),
                        requestParameters(
                                parameterWithName("menuId").description("메뉴 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메세지")
                        )
                ));
    }

    @Test
    void 카트가_비어있는_상태에서_메뉴를_삭제할_경우() throws Exception {
        // given
        given(cartService.removeMenu(anyLong(), anyInt())).willThrow(NOT_FOUND_MENU.notFoundException());

        // when
        ResultActions resultActions = mockMvc.perform(delete("/api/carts/menu")
                .header(HttpHeaders.AUTHORIZATION, token)
                .param("menuIdx", "1"));

        // then
        resultActions.andExpect(status().isNotFound())
                .andDo(document("menu-remove-fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        ),
                        requestParameters(
                                parameterWithName("menuIdx").description("카트 INDEX (리스트 번호)")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메세지")
                        )
                ));
    }

    @Test
    void 카트가_비어있는_상태에서_메뉴를_가져올_경우() throws Exception {
        // given
        given(cartService.getMenu(anyLong(), anyInt())).willThrow(NOT_FOUND_MENU.notFoundException());

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/carts/menu")
                .header(HttpHeaders.AUTHORIZATION, token)
                .param("menuIdx", "1"));

        // then
        resultActions.andExpect(status().isNotFound())
                .andDo(document("menu-get-fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        ),
                        requestParameters(
                                parameterWithName("menuIdx").description("카트 INDEX (리스트 번호)")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메세지")
                        )
                ));
    }

    @Test
    void 카트는_메뉴를_담을때_10개를_초과할_수_없다() throws Exception {
        // given
        given(cartService.addMenu(anyLong(), anyLong()))
                .willThrow(new RuntimeException("카트는 메뉴를 " + SIZE.getSize() + "개만 담을 수 있습니다."));

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/carts/menu")
                .header(HttpHeaders.AUTHORIZATION, token)
                .param("menuId", "1"));

        // then
        resultActions.andExpect(status().isInternalServerError())
                .andDo(document("menu-add-over-fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        ),
                        requestParameters(
                                parameterWithName("menuId").description("메뉴 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메세지")
                        )
                ));
    }
}