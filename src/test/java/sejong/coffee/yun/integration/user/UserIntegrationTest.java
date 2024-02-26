package sejong.coffee.yun.integration.user;

import net.datafaker.Faker;
import net.datafaker.providers.base.Name;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.StopWatch;
import sejong.coffee.yun.domain.user.*;
import sejong.coffee.yun.integration.MainIntegrationTest;
import sejong.coffee.yun.repository.cart.CartRepository;
import sejong.coffee.yun.repository.coupon.CouponRepository;
import sejong.coffee.yun.repository.redis.NoSqlRepository;
import sejong.coffee.yun.repository.user.UserRepository;
import sejong.coffee.yun.service.command.UserServiceCommand;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.LongStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserIntegrationTest extends MainIntegrationTest {

    @Autowired
    private UserServiceCommand userService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NoSqlRepository noSqlRepository;
    @Autowired
    private CouponRepository couponRepository;

    @AfterEach
    void initDB() {
        cartRepository.clear();
        userRepository.clear();
        noSqlRepository.clear();
    }

    @Nested
    @DisplayName("유저가 회원가입을 진행")
    class SignUp {

        @Test
        void 성공적으로_완료_201() throws Exception {
            // given
            String s = toJson(signUpRequest());

            // when
            ResultActions resultActions = mockMvc.perform(post(MEMBER_API_PATH)
                    .content(s)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.memberId").isNumber())
                    .andExpect(jsonPath("$.email").value(signUpRequest().email()))
                    .andExpect(jsonPath("$.name").value(signUpRequest().name()))
                    .andDo(document("member-create",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    getUserRequests()
                            ),
                            responseFields(
                                    getUserResponses()
                            )
                    ));
        }

        @Test
        @Sql("/sql/user.sql")
        void 중복되는_가입을_진행할_경우_400() throws Exception {
            // given
            String s = toJson(signUpRequest());

            // when
            ResultActions resultActions = mockMvc.perform(post(MEMBER_API_PATH)
                    .content(s)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isInternalServerError())
                    .andDo(document("member-create-duplicate",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    getUserRequests()
                            ),
                            responseFields(
                                    getFailResponses()
                            )
                    ));
        }

        @Test
        void 요구하는_입력이_아닐경우_400() throws Exception {
            // given
            String s = toJson(badSignUpRequest());

            // when
            ResultActions resultActions = mockMvc.perform(post(MEMBER_API_PATH)
                    .content(s)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest())
                    .andDo(document("member-create-validation-fail",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    getUserRequests()
                            ),
                            responseFields(
                                    getFailResponses()
                            )
                    ));
        }

        @Test
        @Sql("/sql/user.sql")
        void 이메일이_중복일_경우() throws Exception {
            // given
            String email = "qwer1234@naver.com";

            // when
            ResultActions resultActions = mockMvc.perform(get(MEMBER_API_PATH + "/duplication/email")
                    .param("email", email));

            // then
            resultActions.andExpect(status().isInternalServerError())
                    .andDo(document("member-duplicate-check-email-fail",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestParameters(
                                    parameterWithName("email").description("유저 이메일")
                            ),
                            responseFields(
                                    getFailResponses()
                            )
                            ));
        }

        @Test
        @Sql("/sql/user.sql")
        void 이름이_중복일_경우() throws Exception {
            // given
            String name = "홍길동";

            // when
            ResultActions resultActions = mockMvc.perform(get(MEMBER_API_PATH + "/duplication/name")
                    .param("name", name));

            // then
            resultActions.andExpect(status().isInternalServerError())
                    .andDo(document("member-duplicate-check-name-fail",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestParameters(
                                    parameterWithName("name").description("유저 이름")
                            ),
                            responseFields(
                                    getFailResponses()
                            )
                            ));
        }
    }

    @Nested
    @DisplayName("유저가 로그인을 진행")
    @Sql(value = "/sql/user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    class SignIn {

        @Test
        void 유저가_로그인에_성공한다_200() throws Exception {
            // given
            String s = toJson(signInRequest());

            // when
            ResultActions resultActions = mockMvc.perform(post(MEMBER_API_PATH + "/sign-in")
                    .content(s)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isOk())
                    .andDo(document("sign-in",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    fieldWithPath("email").type(JsonFieldType.STRING).description("유저 이메일"),
                                    fieldWithPath("password").type(JsonFieldType.STRING).description("유저 비밀번호")
                            ),
                            responseHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            )
                    ));
        }

        @Test
        void 아이디_또는_패스워드가_맞지_않는_경우_400() throws Exception {
            // given
            String s = toJson(badSignInRequest("qwer1234@naver.com", "asdf1234@A"));

            // when
            ResultActions resultActions = mockMvc.perform(post(MEMBER_API_PATH + "/sign-in")
                    .content(s)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest())
                    .andDo(document("sign-in-not-match-fail",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    fieldWithPath("email").type(JsonFieldType.STRING).description("유저 이메일"),
                                    fieldWithPath("password").type(JsonFieldType.STRING).description("유저 비밀번호")
                            ),
                            responseFields(
                                    getFailResponses()
                            )
                    ));
        }

        @Test
        void 요구하는_입력이_아닐_경우_400() throws Exception {
            // given
            String s = toJson(badSignInRequest("gfdgsfd", "gfdsg"));

            // when
            ResultActions resultActions = mockMvc.perform(post(MEMBER_API_PATH + "/sign-in")
                    .content(s)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest())
                    .andDo(document("sign-in-validation-fail",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    fieldWithPath("email").type(JsonFieldType.STRING).description("유저 이메일"),
                                    fieldWithPath("password").type(JsonFieldType.STRING).description("유저 비밀번호")
                            ),
                            responseFields(
                                    getFailResponses()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("유저가 이름, 이메일, 비밀번호 수정 진행")
    @Sql(value = "/sql/user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    class Update {

        String token = "";

        @BeforeEach
        void loginInit() throws Exception {
            ResultActions resultActions = mockMvc.perform(post(MEMBER_API_PATH + "/sign-in")
                    .content(toJson(signInRequest()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

            token += resultActions.andReturn().getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        }

        @Test
        void 유저가_이름을_변경한다_200() throws Exception {
            // given
            String s = toJson(updateNameRequest());

            System.out.println(token);

            // when
            ResultActions resultActions = mockMvc.perform(patch(MEMBER_API_PATH + "/name")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .content(s)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(updateNameRequest().updateName()))
                    .andDo(document("member-update-name",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            ),
                            requestFields(
                                    fieldWithPath("updateName").type(JsonFieldType.STRING).description("변경할 이름")
                            ),
                            responseFields(
                                    getUserResponses()
                            )
                    ));
        }

        @Test
        void 유저가_이메일을_변경한다_200() throws Exception {
            // given
            String s = toJson(updateEmailRequest());

            // when
            ResultActions resultActions = mockMvc.perform(patch(MEMBER_API_PATH + "/email")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .content(s)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(updateEmailRequest().updateEmail()))
                    .andDo(document("member-update-email",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            ),
                            requestFields(
                                    fieldWithPath("updateEmail").type(JsonFieldType.STRING).description("변경할 이메일")
                            ),
                            responseFields(
                                    getUserResponses()
                            )
                    ));
        }

        @Test
        void 유저가_비밀번호를_변경한다_200() throws Exception {
            // given
            String s = toJson(updatePasswordRequest());

            // when
            ResultActions resultActions = mockMvc.perform(patch(MEMBER_API_PATH + "/password")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .content(s)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isOk())
                    .andDo(document("member-update-password",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            ),
                            requestFields(
                                    fieldWithPath("updatePassword").type(JsonFieldType.STRING).description("변경할 비밀번호")
                            )
                    ));
        }

        @Test
        void 유저가_수정할_입력값을_실수_했을때_400() throws Exception {
            // given
            String s = toJson(badUpdateRequest());

            // when
            ResultActions resultActions = mockMvc.perform(patch(MEMBER_API_PATH + "/name")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .content(s)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest())
                    .andDo(document("member-update-validation-fail",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            ),
                            requestFields(
                                     fieldWithPath("updateName").type(JsonFieldType.STRING).description("이메일, 이름, 비밀번호 공통되는 사항 에러")
                            ),
                            responseFields(
                                    getFailResponses()
                            )
                            ));

        }
    }

    @Nested
    @DisplayName("유저가 로그아웃을 진행")
    @Sql(value = "/sql/user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    class SignOut {

        String token = "";

        @BeforeEach
        void loginInit() throws Exception {
            ResultActions resultActions = mockMvc.perform(post(MEMBER_API_PATH + "/sign-in")
                    .content(toJson(signInRequest()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

            token += resultActions.andReturn().getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        }

        @Test
        void 로그아웃에_성공한다_200() throws Exception {
            // given

            // when
            ResultActions resultActions = mockMvc.perform(post(MEMBER_API_PATH + "/sign-out")
                    .header(HttpHeaders.AUTHORIZATION, token));

            // then
            resultActions.andExpect(status().isOk())
                    .andDo(document("sign-out",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            ),
                            responseBody()
                    ));
        }

        @Test
        void 유효하지_않은_토큰이_넘어간다_400() throws Exception {
            // given

            // when
            ResultActions resultActions = mockMvc.perform(post(MEMBER_API_PATH + "/sign-out")
                    .header(HttpHeaders.AUTHORIZATION, "bearer invalid token!!!"));

            // then
            resultActions.andExpect(status().isBadRequest())
                    .andDo(document("sign-out-fail",
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
    }

    @Nested
    @DisplayName("유저가 회원 탈퇴를 진행")
    @Sql(value = "/sql/user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    class Remove {

        String token = "";

        @BeforeEach
        void loginInit() throws Exception {
            ResultActions resultActions = mockMvc.perform(post(MEMBER_API_PATH + "/sign-in")
                    .content(toJson(signInRequest()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

            token += resultActions.andReturn().getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        }

        @Test
        void 회원_탈퇴에_성공한다_204() throws Exception {
            // given

            // when
            ResultActions resultActions = mockMvc.perform(delete(MEMBER_API_PATH)
                    .header(HttpHeaders.AUTHORIZATION, token));

            // then
            resultActions.andExpect(status().isNoContent())
                    .andDo(document("member-remove",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            )
                    ));
        }

        @Test
        void 잘못된_토큰으로_실패_400() throws Exception {
            // given

            // when
            ResultActions resultActions = mockMvc.perform(delete(MEMBER_API_PATH)
                    .header(HttpHeaders.AUTHORIZATION, "bearer invalid token"));

            // then
            resultActions.andExpect(status().isBadRequest())
                    .andDo(document("member-remove-fail",
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
    }

    @Nested
    @DisplayName("유저 대용량 처리")
    @Sql(value = "/sql/coupon.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    class UserLargeCapacityTest {

        final int MAX = 100000;

        @BeforeEach
        void init() throws Exception {
            Faker faker = new Faker(new Locale("ko"));

            Coupon coupon = Coupon.builder()
                    .name("월별 쿠폰")
                    .quantity(0)
                    .discountRate(0.1)
                    .identityNumber("1234-1234-1234-1234")
                    .couponUse(CouponUse.NO)
                    .createAt(LocalDateTime.now())
                    .expireAt(LocalDateTime.now())
                    .build();

            Address address = new Address("서울시", "광진구", "세종대1번출구", "123-123");

            Coupon saveCoupon = couponRepository.save(coupon);
            Name name = faker.name();

            var t = LongStream.iterate(0, i -> i + 1)
                    .limit(100)
                    .mapToObj(i -> CompletableFuture.supplyAsync(() -> {

                        String expression = faker.expression("#{options.option 'BRONZE', 'SILVER', 'GOLD', 'PLATINUM', 'DIAMOND'}");

                        var deliveries = LongStream.rangeClosed(1, MAX / 1000)
                                .mapToObj(id -> Member.from(id + i * MAX, Member.create(name.name(), "qwer1234@A", "swager253@naver.com",
                                                UserRank.valueOf(expression), address, Money.ZERO,
                                                this.mapToOrderCount(UserRank.valueOf(expression)), saveCoupon)
                                        )
                                )
                                .toList();

                        userRepository.bulkInsert(deliveries);
                        return 1;
                    }))
                    .toList();

            t.stream().map(CompletableFuture::join).forEach((ignore) -> {});
        }

        @AfterEach
        void initDB() {
            userRepository.bulkDelete();
        }

        @Test
        void 유저들_등급_일괄_업데이트() {
            // given
            List<UserRank> userRanks = Arrays.asList(UserRank.BRONZE, UserRank.SILVER, UserRank.GOLD, UserRank.PLATINUM);
            StopWatch stopWatch = new StopWatch();

            // when
            stopWatch.start();
            userRanks.forEach(userRank -> userRepository.updateUserRank(mapToOrderCount(userRank), userRank));
            stopWatch.stop();

            // then
            System.out.println("execution time -> " + stopWatch.getTotalTimeSeconds());
        }

        @Test
        void 유저들_등급_일괄_업데이트_병렬처리() {
            // given
            List<UserRank> userRanks = Arrays.asList(UserRank.BRONZE, UserRank.SILVER, UserRank.GOLD, UserRank.PLATINUM);
            StopWatch stopWatch = new StopWatch();

            // when
            stopWatch.start();
            var v = userRanks.stream()
                    .map(userRank -> CompletableFuture.supplyAsync(() -> userRepository.updateUserRank(mapToOrderCount(userRank), userRank)));

            long sum = v.map(CompletableFuture::join).mapToLong(i -> i).sum();
            stopWatch.stop();

            // then
            System.out.println("execution time -> " + stopWatch.getTotalTimeSeconds());
            System.out.println("count -> "+ sum);
        }

        @Test
        void 유저들_등급_업데이트_Dirty_Check() {
            // given
            StopWatch stopWatch = new StopWatch();

            // when
            stopWatch.start();
            userService.updateAllUserRank();
            stopWatch.stop();

            // then
            System.out.println("execution time -> " + stopWatch.getTotalTimeSeconds());
        }

        private int mapToOrderCount(UserRank userRank) {
            if(Objects.equals(userRank, UserRank.BRONZE)) {
                return 1;
            } else if(Objects.equals(userRank, UserRank.SILVER)) {
                return 6;
            } else if(Objects.equals(userRank, UserRank.GOLD)) {
                return 11;
            } else if(Objects.equals(userRank, UserRank.PLATINUM)) {
                return 16;
            } else {
                return 100;
            }
        }
    }
}
