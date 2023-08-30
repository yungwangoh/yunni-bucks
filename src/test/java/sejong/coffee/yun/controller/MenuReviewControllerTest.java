package sejong.coffee.yun.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import sejong.coffee.yun.domain.order.menu.*;
import sejong.coffee.yun.domain.user.Address;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.domain.user.UserRank;
import sejong.coffee.yun.dto.review.menu.MenuReviewDto;
import sejong.coffee.yun.dto.review.menu.MenuReviewPageDto;
import sejong.coffee.yun.jwt.JwtProvider;
import sejong.coffee.yun.mapper.CustomMapper;
import sejong.coffee.yun.service.MenuReviewService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_MENU_REVIEW;

@WebMvcTest(MenuReviewController.class)
class MenuReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MenuReviewService menuReviewService;
    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private CustomMapper customMapper;

    static MenuReview menuReview;
    static MenuReviewDto.Response response;
    static MenuReviewDto.Request request;
    static MenuReviewPageDto.Response pageResponse;
    static Member saveMember;
    static Menu saveMenu;
    static String token;
    static MenuReviewDto.Update.Response updateResponse;
    static PageImpl<MenuReview> page;

    @BeforeAll
    static void init() {
        String comment = "괜찮아요";

        Member member = Member.builder()
                .address(new Address("서울시", "광진구", "화양동", "123-432"))
                .userRank(UserRank.BRONZE)
                .name("홍길동")
                .password("qwer1234@A")
                .money(Money.ZERO)
                .email("qwer123@naver.com")
                .orderCount(0)
                .build();

        saveMember = Member.from(1L, member);

        Nutrients nutrients = new Nutrients(80, 80, 80, 80);
        Beverage beverage = Beverage.builder()
                .description("에티오피아산 커피")
                .title("커피")
                .price(Money.initialPrice(new BigDecimal(1000)))
                .nutrients(nutrients)
                .menuSize(MenuSize.M)
                .now(LocalDateTime.now())
                .build();

        saveMenu = Beverage.from(1L, beverage);

        menuReview = MenuReview.create(comment, saveMember, saveMenu, LocalDateTime.now());

        response = new MenuReviewDto.Response(menuReview);
        request = new MenuReviewDto.Request(comment);

        PageRequest pr = PageRequest.of(0, 10);
        List<MenuReview> review = List.of(menuReview);
        page = new PageImpl<>(review, pr, review.size());
        pageResponse = new MenuReviewPageDto.Response(page);

        token = "bearer accessToken";

        updateResponse = new MenuReviewDto.Update.Response(1L, "맛있어요");
    }

    @Test
    void 메뉴_리뷰_201() throws Exception {
        // given
        given(menuReviewService.create(anyLong(), anyLong(), anyString(), any())).willReturn(menuReview);
        given(customMapper.map(any(), any())).willReturn(response);

        String s = toJson(request);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/{menuId}/reviews", 1L)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(s)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isCreated())
                .andExpect(content().json(toJson(response)));
    }

    @Test
    void 메뉴_리뷰_찾기_200() throws Exception {
        // given
        given(menuReviewService.findReview(anyLong())).willReturn(menuReview);
        given(customMapper.map(any(), any())).willReturn(response);

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/reviews/{reviewId}", 1L));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().json(toJson(response)));
    }

    @Test
    void 메뉴_리뷰_찾기_404() throws Exception {
        // given
        given(menuReviewService.findReview(anyLong())).willThrow(NOT_FOUND_MENU_REVIEW.notFoundException());

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/reviews/{reviewId}", 1L));

        // then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void 메뉴_리뷰_삭제_204() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(delete("/api/reviews/{reviewId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isNoContent());
        then(menuReviewService).should(times(1)).delete(0L, 1L);
    }

    @Test
    void 메뉴_리뷰_삭제_404() throws Exception {
        // given
        doThrow(NOT_FOUND_MENU_REVIEW.notFoundException()).when(menuReviewService).delete(anyLong(), anyLong());

        // when
        ResultActions resultActions = mockMvc.perform(delete("/api/reviews/{reviewId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void 메뉴_리뷰_수정_200() throws Exception {
        // given
        String comment = "맛있다";
        given(menuReviewService.updateComment(anyLong(), anyLong(), anyString(), any())).willReturn(menuReview);
        given(customMapper.map(any(), any())).willReturn(updateResponse);

        // when
        ResultActions resultActions = mockMvc.perform(patch("/api/reviews/{reviewId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, token)
                .param("comment", comment));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().json(toJson(updateResponse)));
    }

    @Test
    void 페이징_메뉴_리뷰_200() throws Exception {
        // given
        given(menuReviewService.findAllByMemberId(any(), anyLong())).willReturn(page);
        given(customMapper.map(any(), any())).willReturn(pageResponse);

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/reviews/page/{pageNum}", 1L)
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().json(toJson(pageResponse)));
    }

    private String toJson(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }

}