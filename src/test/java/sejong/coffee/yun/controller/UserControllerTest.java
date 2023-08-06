package sejong.coffee.yun.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sejong.coffee.yun.dto.user.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sejong.coffee.yun.domain.exception.ExceptionControl.*;
import static sejong.coffee.yun.message.SuccessOrFailMessage.SUCCESS_DUPLICATE_EMAIL;
import static sejong.coffee.yun.message.SuccessOrFailMessage.SUCCESS_DUPLICATE_NAME;

class UserControllerTest extends BaseUserControllerTest {

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void 회원_등록() throws Exception {
        // given
        given(userService.signUp(any(), any(), any(), any())).willReturn(member);
        given(customMapper.map(any(), any())).willReturn(response);

        String s = objectMapper.writeValueAsString(request);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/members")
                .content(s)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andExpect(jsonPath("$.name").value(member.getName()))
                .andExpect(jsonPath("$.address.city").value(member.getAddress().getCity()))
                .andExpect(jsonPath("$.address.district").value(member.getAddress().getDistrict()))
                .andExpect(jsonPath("$.address.detail").value(member.getAddress().getDetail()))
                .andExpect(jsonPath("$.address.zipCode").value(member.getAddress().getZipCode()))
                .andExpect(jsonPath("$.userRank").value(member.getUserRank().name()))
                .andExpect(jsonPath("$.money.totalPrice").value(member.getMoney().getTotalPrice()))
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
    void 회원_등록_유효성_실패() throws Exception {
        // given
        UserDto.Sign.Up.Request req = new UserDto.Sign.Up.Request(member.getName(),
                "qwereter", member.getPassword(), member.getAddress());

        String s = objectMapper.writeValueAsString(req);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/members")
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
                                getUserFailResponses()
                        )
                ));
    }

    @Test
    void 회원_등록_이메일_중복_실패() throws Exception {
        // given
        given(userService.signUp(any(), any(), any(), any())).willThrow(DUPLICATE_USER_EMAIL.duplicatedEmailException());

        String s = objectMapper.writeValueAsString(request);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/members")
                .content(s)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DUPLICATE_USER_EMAIL.getMessage()))
                .andDo(document("member-create-duplicate-email-fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                getUserRequests()
                        ),
                        responseFields(
                                getUserFailResponses()
                        )
                ));
    }

    @Test
    void 회원_등록_이름_중복_실패() throws Exception {
        // given
        given(userService.signUp(any(), any(), any(), any())).willThrow(DUPLICATE_USER_NAME.duplicatedNameException());

        String s = objectMapper.writeValueAsString(request);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/members")
                .content(s)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DUPLICATE_USER_NAME.getMessage()))
                .andDo(document("member-create-duplicate-name-fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                getUserRequests()
                        ),
                        responseFields(
                                getUserFailResponses()
                        )
                ));
    }

    @Test
    void 회원_리스트() throws Exception {
        // given
        given(userService.findAll()).willReturn(List.of(member));

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/members/list")
                .header(HttpHeaders.AUTHORIZATION, token));

        // when
        resultActions.andExpect(status().isOk())
                .andDo(document("member-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        ),
                        responseFields(
                                fieldWithPath("[].memberId").type(JsonFieldType.NUMBER).description(1),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("홍길동"),
                                fieldWithPath("[].email").type(JsonFieldType.STRING).description("qwer1234@naver.com"),
                                fieldWithPath("[].address.city").type(JsonFieldType.STRING).description("서울시"),
                                fieldWithPath("[].address.district").type(JsonFieldType.STRING).description("광진구"),
                                fieldWithPath("[].address.detail").type(JsonFieldType.STRING).description("능동로 209 세종대학교"),
                                fieldWithPath("[].address.zipCode").type(JsonFieldType.STRING).description("123-123"),
                                fieldWithPath("[].userRank").type(JsonFieldType.STRING).description("BRONZE"),
                                fieldWithPath("[].money.totalPrice").type(JsonFieldType.NUMBER).description("0"),
                                fieldWithPath("[].createAt").description("생성 시간"),
                                fieldWithPath("[].updateAt").description("수정 시간")
                        )
                ));
    }

    @Test
    void 회원_찾기() throws Exception {
        // given
        given(userService.findMember(any())).willReturn(member);
        given(customMapper.map(any(), any())).willReturn(new UserDto.Response(member));

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/members")
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andExpect(jsonPath("$.name").value(member.getName()))
                .andExpect(jsonPath("$.address.city").value(member.getAddress().getCity()))
                .andExpect(jsonPath("$.address.district").value(member.getAddress().getDistrict()))
                .andExpect(jsonPath("$.address.detail").value(member.getAddress().getDetail()))
                .andExpect(jsonPath("$.address.zipCode").value(member.getAddress().getZipCode()))
                .andExpect(jsonPath("$.userRank").value(member.getUserRank().name()))
                .andExpect(jsonPath("$.money.totalPrice").value(member.getMoney().getTotalPrice()))
                .andDo(document("member-find",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        ),
                        responseFields(
                                getUserResponses()
                        )
                ));
    }

    @Test
    void 회원_찾기_실패() throws Exception {
        // given
        given(userService.findMember(any())).willThrow(NOT_FOUND_USER.notFoundException());

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/members")
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isNotFound())
                .andDo(document("member-find-fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        ),
                        responseFields(
                                getUserFailResponses()
                        )
                ));
    }

    @Test
    void 회원_삭제() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(delete("/api/members")
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isNoContent())
                .andDo(document("member-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        )
                ));
    }

    @Test
    void 회원_식제_실패() throws Exception {
        // given
        willThrow(NOT_FOUND_USER.notFoundException()).given(userService).deleteMember(any());

        // when
        ResultActions resultActions = mockMvc.perform(delete("/api/members")
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isNotFound())
                .andDo(document("member-delete-fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        ),
                        responseFields(
                                getUserFailResponses()
                        )
                ));
    }

    @Test
    void 회원_이메일_수정() throws Exception {
        // given
        String updateEmail = "asdf1234@naver.com";

        given(userService.updateEmail(any(), any())).willReturn(member);
        given(customMapper.map(any(), any())).willReturn(new UserDto.Response(updateEmailMember));

        String s = objectMapper.writeValueAsString(new UserDto.Update.Email.Request(updateEmail));

        // when
        ResultActions resultActions = mockMvc.perform(patch("/api/members/email")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(s));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(updateEmail))
                .andDo(document("member-update-email",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        ),
                        requestFields(
                                fieldWithPath("updateEmail").type(JsonFieldType.STRING).description("수정할 이메일")
                        ),
                        responseFields(
                                getUserResponses()
                        )
                ));
    }

    @Test
    void 회원_이름_수정() throws Exception {
        // given
        String updateName = "홍홍길동";

        given(userService.updateEmail(any(), any())).willReturn(member);
        given(customMapper.map(any(), any())).willReturn(new UserDto.Response(updateNameMember));

        String s = objectMapper.writeValueAsString(new UserDto.Update.Name.Request(updateName));

        // when
        ResultActions resultActions = mockMvc.perform(patch("/api/members/name")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(s));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updateName))
                .andDo(document("member-update-name",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        ),
                        requestFields(
                                fieldWithPath("updateName").type(JsonFieldType.STRING).description("수정할 이름")
                        ),
                        responseFields(
                                getUserResponses()
                        )
                ));
    }

    @Test
    void 로그인() throws Exception {
        // given
        String accessToken = "token";

        given(userService.signIn(anyString(), anyString())).willReturn(accessToken);
        given(customMapper.map(any(), any())).willReturn(new UserDto.Sign.In.Response(accessToken));

        String s = objectMapper.writeValueAsString(
                new UserDto.Sign.In.Request(member.getEmail(), member.getPassword()));

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/members/sign-in")
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(s)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(accessToken))
                .andDo(document("sign-in",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        ),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("회원 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("토큰")
                        )
                ));
    }

    @Test
    void 로그인_아이디_비밀번호가_다를_경우_실패() throws Exception {
        // given

        given(userService.signIn(anyString(), anyString())).willThrow(NOT_MATCH_USER.notMatchUserException());

        String s = objectMapper.writeValueAsString(
                new UserDto.Sign.In.Request(member.getEmail(), member.getPassword()));

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/members/sign-in")
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(s)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andDo(document("sign-in-not-match-fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        ),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("회원 비밀번호")
                        ),
                        responseFields(
                                getUserFailResponses()
                        )
                ));
    }

    @Test
    void 로그_아웃() throws Exception {
        // given
        String accessToken = "token";

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/members/sign-out")
                .header(HttpHeaders.AUTHORIZATION, accessToken));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("sign-out",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        )
                ));
    }

    @Test
    void 로그_아웃_실패_토큰_만료() throws Exception {
        // given
        String accessToken = "token";

        given(userService.signOut(anyString(), any())).willThrow(TOKEN_EXPIRED.tokenExpiredException());

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/members/sign-out")
                .header(HttpHeaders.AUTHORIZATION, accessToken));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andDo(document("sign-out-token-expire",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description(token)
                        ),
                        responseFields(
                                getUserFailResponses()
                        )
                ));
    }

    @Test
    void 회원_이메일_중복() throws Exception {
        // given
        String email = "qwer1234@naver.com";
        given(userService.duplicateEmail(anyString())).willReturn(SUCCESS_DUPLICATE_EMAIL.getMessage());

        // when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/members/duplication/email")
                .param("email", email));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(SUCCESS_DUPLICATE_EMAIL.getMessage()))
                .andDo(document("member-duplicate-email",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestParameters(
                          parameterWithName("email").description("이메일")
                    )
                ));
    }

    @Test
    void 회원_이메일_중복_검증_실패() throws Exception {
        String email = "qwer1234@naver.com";
        given(userService.duplicateEmail(anyString())).willThrow(DUPLICATE_USER_EMAIL.duplicatedEmailException());

        // when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/members/duplication/email")
                .param("email", email));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andDo(document("member-duplicate-check-email-fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("email").description("이메일")
                        ),
                        responseFields(
                                getUserFailResponses()
                        )
                ));
    }

    @Test
    void 회원_이름_중복() throws Exception {
        // given
        String name = "홍길동";
        given(userService.duplicateName(anyString())).willReturn(SUCCESS_DUPLICATE_NAME.getMessage());

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/members/duplication/name")
                .param("name", name));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string(SUCCESS_DUPLICATE_NAME.getMessage()))
                .andDo(document("member-duplicate-name",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("name").description("이름")
                        )
                ));
    }

    @Test
    void 회원_이름_중복_검증_실패() throws Exception {
        String name = "홍길동";
        given(userService.duplicateName(anyString())).willThrow(DUPLICATE_USER_NAME.duplicatedNameException());

        // when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/members/duplication/name")
                .param("name", name));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andDo(document("member-duplicate-check-name-fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("name").description("이름")
                        ),
                        responseFields(
                                getUserFailResponses()
                        )
                ));
    }
}