package sejong.coffee.yun.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import sejong.coffee.yun.dto.user.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sejong.coffee.yun.domain.exception.ExceptionControl.*;
import static sejong.coffee.yun.message.SuccessOrFailMessage.SUCCESS_DUPLICATE_EMAIL;
import static sejong.coffee.yun.message.SuccessOrFailMessage.SUCCESS_DUPLICATE_NAME;

class UserControllerTest extends BaseUserControllerTest {

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
                .andExpect(jsonPath("$.money.totalPrice").value(member.getMoney().getTotalPrice()));
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
                .andExpect(jsonPath("message").value(INPUT_ERROR.getMessage()));
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
                .andExpect(jsonPath("$.message").value(DUPLICATE_USER_EMAIL.getMessage()));
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
                .andExpect(jsonPath("$.message").value(DUPLICATE_USER_NAME.getMessage()));
    }

    @Test
    void 회원_리스트() throws Exception {
        // given
        given(userService.findAll()).willReturn(List.of(member));

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/members/list")
                .header(HttpHeaders.AUTHORIZATION, token));

        // when
        resultActions.andExpect(status().isOk());
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
                .andExpect(jsonPath("$.money.totalPrice").value(member.getMoney().getTotalPrice()));
    }

    @Test
    void 회원_찾기_실패() throws Exception {
        // given
        given(userService.findMember(any())).willThrow(NOT_FOUND_USER.notFoundException());

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/members")
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void 회원_삭제() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(delete("/api/members")
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isNoContent());
    }

    @Test
    void 회원_삭제_실패() throws Exception {
        // given
        willThrow(NOT_FOUND_USER.notFoundException()).given(userService).deleteMember(any());

        // when
        ResultActions resultActions = mockMvc.perform(delete("/api/members")
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        resultActions.andExpect(status().isNotFound());
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
                .andExpect(jsonPath("$.email").value(updateEmail));
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
                .andExpect(jsonPath("$.name").value(updateName));
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
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, "bearer " + accessToken));
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
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void 로그_아웃() throws Exception {
        // given
        String accessToken = "token";

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/members/sign-out")
                .header(HttpHeaders.AUTHORIZATION, accessToken));

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void 로그_아웃_실패_토큰_만료() throws Exception {
        // given
        String accessToken = "token";

        given(userService.signOut(anyString(), any())).willThrow(TOKEN_EXPIRED.tokenExpiredException());

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/members/sign-out")
                .header(HttpHeaders.AUTHORIZATION, accessToken));

        // then
        resultActions.andExpect(status().isBadRequest());
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
                .andDo(print())
                .andExpect(content().string(SUCCESS_DUPLICATE_EMAIL.getMessage()));
    }

    @Test
    void 회원_이메일_중복_검증_실패() throws Exception {
        String email = "qwer1234@naver.com";
        given(userService.duplicateEmail(anyString())).willThrow(DUPLICATE_USER_EMAIL.duplicatedEmailException());

        // when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/members/duplication/email")
                .param("email", email));

        // then
        resultActions.andExpect(status().isBadRequest());
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
                .andExpect(content().string(SUCCESS_DUPLICATE_NAME.getMessage()));
    }

    @Test
    void 회원_이름_중복_검증_실패() throws Exception {
        String name = "홍길동";
        given(userService.duplicateName(anyString())).willThrow(DUPLICATE_USER_NAME.duplicatedNameException());

        // when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/members/duplication/name")
                .param("name", name));

        // then
        resultActions.andExpect(status().isBadRequest());
    }
    private String toJson(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }
}