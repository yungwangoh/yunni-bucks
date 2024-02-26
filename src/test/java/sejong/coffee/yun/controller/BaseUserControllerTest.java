package sejong.coffee.yun.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import sejong.coffee.yun.controller.advise.ErrorDetectAdvisor;
import sejong.coffee.yun.domain.user.Address;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.domain.user.UserRank;
import sejong.coffee.yun.dto.user.UserDto;
import sejong.coffee.yun.jwt.JwtProvider;
import sejong.coffee.yun.mapper.CustomMapper;
import sejong.coffee.yun.service.command.UserService;

@WebMvcTest(UserController.class)
@Disabled
public class BaseUserControllerTest {

    @Autowired
    ErrorDetectAdvisor errorDetectAdvisor;
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
}
