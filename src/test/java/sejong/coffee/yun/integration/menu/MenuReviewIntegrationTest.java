package sejong.coffee.yun.integration.menu;

import net.datafaker.Faker;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.order.menu.MenuReview;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.integration.MainIntegrationTest;
import sejong.coffee.yun.repository.menu.MenuRepository;
import sejong.coffee.yun.repository.review.menu.MenuReviewRepository;
import sejong.coffee.yun.repository.user.UserRepository;
import sejong.coffee.yun.service.MenuReviewService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MenuReviewIntegrationTest extends MainIntegrationTest {

    @Autowired
    private MenuReviewService menuReviewService;
    @Autowired
    private MenuReviewRepository menuReviewRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private UserRepository userRepository;

    @Nested
    @DisplayName("유저가 로그인하고 메뉴 리뷰를 작성한다")
    @Sql(value = {"/sql/user.sql", "/sql/menu.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/truncate.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    class ReviewTest {

        String token;

        @BeforeEach
        void init() throws Exception{
            token = signInModule();
        }

        @AfterEach
        void initDB() {
            menuReviewRepository.clear();
        }

        @Test
        void 유저가_메뉴리뷰를_작성한다_201() throws Exception {
            // given
            String s = toJson(menuReviewRequest());

            // when
            ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.post(MENU_REVIEW_API_PATH + "/{menuId}/reviews", 1L)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .content(s)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.comment").value(menuReviewRequest().comment()))
                    .andDo(document("menu-review",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            ),
                            pathParameters(
                                    parameterWithName("menuId").description("메뉴 ID")
                            ),
                            requestFields(
                                    fieldWithPath("comment").type(JsonFieldType.STRING).description("코멘트")
                            ),
                            responseFields(
                                    getMenuReviewResponse()
                            )
                    ));
        }

        @Test
        void 유저가_자신이_쓴_리뷰를_조회한다_200() throws Exception {
            // given
            MenuReview menuReview = menuReviewService.create(1L, 1L, "맛있어요", LocalDateTime.now());

            // when
            ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get(MENU_REVIEW_API_PATH + "/reviews/{reviewId}", menuReview.getId())
                    .header(HttpHeaders.AUTHORIZATION, token));

            // then
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.comment").value("맛있어요"))
                    .andDo(document("menu-review-find",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            ),
                            pathParameters(
                                    parameterWithName("reviewId").description("리뷰 ID")
                            ),
                            responseFields(
                                    getMenuReviewResponse()
                            )
                    ));
        }

        @Test
        void 유저가_메뉴리뷰를_삭제한다_204() throws Exception {
            // given
            MenuReview menuReview = menuReviewService.create(1L, 1L, "맛있어요", LocalDateTime.now());

            // when
            ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.delete(MENU_REVIEW_API_PATH + "/reviews/{reviewId}", menuReview.getId())
                    .header(HttpHeaders.AUTHORIZATION, token));

            // then
            resultActions.andExpect(status().isNoContent())
                    .andDo(document("menu-review-delete",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            ),
                            pathParameters(
                                    parameterWithName("reviewId").description("리뷰 ID")
                            )
                    ));
        }

        @Test
        void 유저가_자신이_쓴_리뷰를_수정한다_200() throws Exception {
            // given
            MenuReview menuReview = menuReviewService.create(1L, 1L, "맛있어요", LocalDateTime.now());

            String s = toJson(menuReviewUpdateRequest());

            // when
            ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.patch(MENU_REVIEW_API_PATH + "/reviews/{reviewId}", menuReview.getId())
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .content(s)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.comment").value(menuReviewUpdateRequest().comment()))
                    .andDo(document("menu-review-update",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            ),
                            pathParameters(
                                    parameterWithName("reviewId").description("리뷰 ID")
                            ),
                            requestFields(
                                    fieldWithPath("comment").description("코멘트")
                            ),
                            responseFields(
                                    getMenuReviewUpdateResponse()
                            )
                    ));
        }

        @Test
        void 잘못된_리뷰ID인_경우_404() throws Exception {
            // given
            menuReviewService.create(1L, 1L, "맛있어요", LocalDateTime.now());

            String s = toJson(menuReviewUpdateRequest());

            // when
            ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.patch(MENU_REVIEW_API_PATH + "/reviews/{reviewId}", 100L)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .content(s)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isNotFound())
                    .andDo(document("invalid-review-id",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    getFailResponses()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("메뉴 리뷰 대용량 테스트")
    @Sql(value = {"/sql/user.sql", "/sql/menu.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/truncate.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    class MenuReviewLargeCapacityTest {

        String token;
        List<MenuReview> list = new ArrayList<>();

        @BeforeEach
        void init() throws Exception{
            token = signInModule();

            Faker faker = new Faker(new Locale("ko"));

            Menu menu = menuRepository.findById(1L);
            Member member = userRepository.findById(1L);

            for(int i = 0; i < 1; i++) {

                for (int j = 1; j <= 100000; j++) {
                    String sentence = faker.lorem().sentence();

                    MenuReview menuReview = MenuReview.builder()
                            .id((long) j + (100000 * i))
                            .comments(sentence)
                            .menu(menu)
                            .member(member)
                            .now(LocalDateTime.now())
                            .build();

                    list.add(menuReview);
                }
                menuReviewRepository.bulkInsert(list.size(), list);

                list.clear();
            }
        }

        @AfterEach
        void initDB() {
            menuReviewRepository.bulkDelete();
        }

        @Test
        void 메뉴_리뷰_전문검색_테스트_10만_데이터_LIKE_방식() {
            // given
            String searchString = "법률";

            Long beforeTime = System.currentTimeMillis();
            // when
            List<MenuReview> comments = menuReviewService.findByComments(searchString);

            Long afterTime = System.currentTimeMillis();

            // then
            //assertTrue(comments.get(0).getComments().contains(searchString));
            System.out.println(comments.size());
            System.out.println("execution time -> " + (double) (afterTime - beforeTime) / 1000);
        }

        @Test
        void 메뉴_리뷰_전문검색_테스트_10만_데이터_FULL_TEXT_SEARCH_방식() {
            // given
            String searchString = "법률";

            Long beforeTime = System.currentTimeMillis();
            // when
            List<MenuReview> menuReviews = menuReviewService.findByFullTextComment(searchString);

            Long afterTime = System.currentTimeMillis();

            // then
            //assertTrue(menuReviews.get(0).getComments().contains(searchString));
            System.out.println(menuReviews.size());
            System.out.println("execution time -> " + (double) (afterTime - beforeTime) / 1000);
        }
    }
}