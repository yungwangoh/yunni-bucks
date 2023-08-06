package sejong.coffee.yun.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import sejong.coffee.yun.domain.user.Address;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.domain.user.UserRank;
import sejong.coffee.yun.dto.user.UserDto;
import sejong.coffee.yun.jwt.JwtProvider;
import sejong.coffee.yun.mapper.CustomMapper;
import sejong.coffee.yun.service.UserService;

import java.util.List;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(UserController.class)
public class BaseUserControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    CustomMapper customMapper;
    @MockBean
    JwtProvider jwtProvider;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    UserService userService;

    static Member member;
    static UserDto.Sign.Up.Request request;
    static UserDto.Response response;
    static Member updateNameMember;
    static Member updateEmailMember;
    static String token;

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

        updateNameMember = Member.from(1L, Member.builder()
                        .email("qwer1234@naver.com")
                        .password("qwer1234@A")
                        .name("홍홍길동")
                        .userRank(UserRank.BRONZE)
                        .money(Money.ZERO)
                        .address(address)
                        .build()
                );

        updateEmailMember = Member.from(1L, Member.builder()
                        .email("asdf1234@naver.com")
                        .password("qwer1234@A")
                        .name("홍길동")
                        .userRank(UserRank.BRONZE)
                        .money(Money.ZERO)
                        .address(address)
                        .build()
                );

        request = new UserDto.Sign.Up.Request(
                member.getName(),
                member.getEmail(),
                member.getPassword(),
                member.getAddress()
        );

        response = new UserDto.Response(
                1L,
                member.getName(),
                member.getEmail(),
                member.getAddress(),
                member.getUserRank(),
                member.getMoney(),
                member.getCreateAt(),
                member.getUpdateAt()
        );

        token = "bearer accessToken";
    }

    static List<FieldDescriptor> getUserResponses() {
        return List.of(
                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("유저 id"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("유저 이름"),
                fieldWithPath("email").type(JsonFieldType.STRING).description("유저 이메일"),
                fieldWithPath("address.city").type(JsonFieldType.STRING).description("시"),
                fieldWithPath("address.district").type(JsonFieldType.STRING).description("군/구"),
                fieldWithPath("address.detail").type(JsonFieldType.STRING).description("상세 주소"),
                fieldWithPath("address.zipCode").type(JsonFieldType.STRING).description("우편 번호"),
                fieldWithPath("userRank").type(JsonFieldType.STRING).description("유저 등급"),
                fieldWithPath("money.totalPrice").type(JsonFieldType.NUMBER).description("유저가 소유한 잔액"),
                fieldWithPath("createAt").description("생성 시간"),
                fieldWithPath("updateAt").description("수정 시간")
        );
    }

    static List<FieldDescriptor> getUserFailResponses() {
        return List.of(
                fieldWithPath("status").type(JsonFieldType.STRING).description("상태 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메세지")
        );
    }

    static List<FieldDescriptor> getUserRequests() {
        return List.of(
                fieldWithPath("name").type(JsonFieldType.STRING).description("유저 이름"),
                fieldWithPath("email").type(JsonFieldType.STRING).description("유저 이메일"),
                fieldWithPath("password").type(JsonFieldType.STRING).description("유저 비밀번호"),
                fieldWithPath("address.city").type(JsonFieldType.STRING).description("시"),
                fieldWithPath("address.district").type(JsonFieldType.STRING).description("군/구"),
                fieldWithPath("address.detail").type(JsonFieldType.STRING).description("상세 주소"),
                fieldWithPath("address.zipCode").type(JsonFieldType.STRING).description("우편 번호")
        );
    }
}
