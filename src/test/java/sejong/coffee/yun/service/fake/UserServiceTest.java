package sejong.coffee.yun.service.fake;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import sejong.coffee.yun.domain.exception.NotFoundException;
import sejong.coffee.yun.domain.exception.NotMatchUserException;
import sejong.coffee.yun.domain.user.Address;
import sejong.coffee.yun.domain.user.Cart;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.jwt.JwtProvider;
import sejong.coffee.yun.mock.repository.CustomValueOperationImpl;
import sejong.coffee.yun.mock.repository.FakeNoSqlRepository;
import sejong.coffee.yun.mock.repository.FakeOrderRepository;
import sejong.coffee.yun.mock.repository.FakeUserRepository;
import sejong.coffee.yun.repository.redis.NoSqlRepository;
import sejong.coffee.yun.service.command.UserServiceCommand;
import sejong.coffee.yun.service.query.UserServiceQuery;
import sejong.coffee.yun.util.password.PasswordUtil;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static sejong.coffee.yun.domain.exception.ExceptionControl.*;
import static sejong.coffee.yun.message.SuccessOrFailMessage.SUCCESS_SIGN_OUT;

@SpringJUnitConfig
@ContextConfiguration(classes = {
        UserServiceQuery.class,
        UserServiceCommand.class,
        FakeUserRepository.class,
        JwtProvider.class,
        FakeNoSqlRepository.class,
        FakeOrderRepository.class,
        CustomValueOperationImpl.class
})
@TestPropertySource(properties = {
        "jwt.key=applicationKey",
        "jwt.expireTime.access=100000",
        "jwt.expireTime.refresh=1000000",
        "jwt.blackList=blackList"
})
public class UserServiceTest {

    @Autowired
    private UserServiceQuery userServiceQuery;
    @Autowired
    private UserServiceCommand userServiceCommand;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private FakeUserRepository userRepository;
    @Autowired
    private NoSqlRepository noSqlRepository;

    private Cart cart;

    @BeforeEach
    void init() {
        cart = Cart.builder()
                .cartItems(new ArrayList<>())
                .build();
    }

    @AfterEach
    void initDB() {
        userRepository.clear();
    }

    @Test
    void 회원_가입() {
        // given
        String name = "홍길동";
        String email = "qwer1234@naver.com";
        String pwd = "qwer1234@A";
        Address address = new Address("서울시", "광진구", "능동로 141", "100-100");

        // when
        Member signUp = userServiceCommand.signUp(name, email, pwd, address);

        // then
        assertThat(signUp.getName()).isEqualTo(name);
        assertThat(signUp.getEmail()).isEqualTo(email);
        assertThat(signUp.getAddress()).isEqualTo(address);
    }

    @Test
    void 회원_찾기() {
        // given
        String name = "홍길동";
        String email = "qwer1234@naver.com";
        String pwd = "qwer1234@A";
        Address address = new Address("서울시", "광진구", "능동로 141", "100-100");

        Member signUp = userServiceCommand.signUp(name, email, pwd, address);

        // when
        Member member = userServiceQuery.findMember(signUp.getId());

        // then
        assertThat(member).isEqualTo(signUp);
    }

    @Test
    void 회원_리스트() {
        // given
        String name = "홍길동";
        String email = "qwer1234@naver.com";
        String pwd = "qwer1234@A";
        Address address = new Address("서울시", "광진구", "능동로 141", "100-100");

        List<Member> members = List.of(userServiceCommand.signUp(name, email, pwd, address));

        // when
        List<Member> list = userServiceQuery.findAll();

        // then
        assertThat(list).isEqualTo(members);
    }

    @Test
    void 로그인() {
        // given
        String name = "홍길동";
        String email = "qwer1234@naver.com";
        String pwd = "qwer1234@A";
        Address address = new Address("서울시", "광진구", "능동로 141", "100-100");

        Member sign = userServiceCommand.signUp(name, email, pwd, address);

        // when
        String token = userServiceCommand.signIn(email, pwd);

        // then
        assertThat(jwtProvider.mapTokenToId("bearer " + token)).isEqualTo(sign.getId());
        assertThat(noSqlRepository.getValues(String.valueOf(sign.getId()))).isNotNull();
    }

    @Test
    void 로그인_입력정보와_가입정보와_다른_경우() {
        // given
        String name = "홍길동";
        String email = "qwer1234@naver.com";
        String pwd = "qwer1234@A";
        Address address = new Address("서울시", "광진구", "능동로 141", "100-100");

        userServiceCommand.signUp(name, email, pwd, address);

        String wrongEmail = "asdfqwer1234@naver.com";
        String wrongPwd = "trewttrgfg@A";

        // when

        // then
        assertThatThrownBy(() -> userServiceCommand.signIn(wrongEmail, wrongPwd))
                .isInstanceOf(NotMatchUserException.class)
                .hasMessageContaining(NOT_MATCH_USER.getMessage());
    }

    @Test
    void 로그아웃() {
        // given
        String name = "홍길동";
        String email = "qwer1234@naver.com";
        String pwd = "qwer1234@A";
        Address address = new Address("서울시", "광진구", "능동로 141", "100-100");

        Member member = userServiceCommand.signUp(name, email, pwd, address);
        String token = userServiceCommand.signIn(email, pwd);

        // when
        String signOut = userServiceCommand.signOut("bearer " + token, member.getId());

        // then
        assertThat(signOut).isEqualTo(SUCCESS_SIGN_OUT.getMessage());
        assertThat(noSqlRepository.getValues(token)).isEqualTo("blackList");
    }

    @Test
    void 회원_이름_변경() {
        // given
        String name = "홍길동";
        String email = "qwer1234@naver.com";
        String pwd = "qwer1234@A";
        Address address = new Address("서울시", "광진구", "능동로 141", "100-100");

        Member signUp = userServiceCommand.signUp(name, email, pwd, address);

        String updateName = "홍홍길동";

        // when
        Member member = userServiceCommand.updateName(signUp.getId(), updateName);

        // then
        assertThat(member.getName()).isEqualTo(updateName);
    }

    @Test
    void 회원_이메일_변경() {
        // given
        String name = "홍길동";
        String email = "qwer1234@naver.com";
        String pwd = "qwer1234@A";
        Address address = new Address("서울시", "광진구", "능동로 141", "100-100");

        Member signUp = userServiceCommand.signUp(name, email, pwd, address);

        String updateEmail = "asdf1234@daum.net";

        // when
        Member member = userServiceCommand.updateEmail(signUp.getId(), updateEmail);

        // then
        assertThat(member.getEmail()).isEqualTo(updateEmail);
    }

    @Test
    void 회원_비밀번호_변경() {
        // given
        String name = "홍길동";
        String email = "qwer1234@naver.com";
        String pwd = "qwer1234@A";
        Address address = new Address("서울시", "광진구", "능동로 141", "100-100");

        Member member = userServiceCommand.signUp(name, email, pwd, address);

        String updatePwd = "asdf1234@A";

        // when
        Member updatePassword = userServiceCommand.updatePassword(member.getId(), updatePwd);

        // then
        assertTrue(PasswordUtil.match(updatePassword.getPassword(), updatePwd));
    }

    @Test
    void 회원_변경_할때_다른_id를_넣은_경우() {
        String updateName = "홍홍홍길동";

        assertThatThrownBy(() -> userServiceCommand.updateEmail(100L, updateName))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    @Test
    void 로그아웃_할_때_엑세스_토큰이_만료된_경우() {
        // given
        String name = "홍길동";
        String email = "qwer1234@naver.com";
        String pwd = "qwer1234@A";
        Address address = new Address("서울시", "광진구", "능동로 141", "100-100");

        Member sign = userServiceCommand.signUp(name, email, pwd, address);

        // when
        String expireToken = "bearer gfjsghjkfsdhgjs";

        // then
        assertThatThrownBy(() -> userServiceCommand.signOut(expireToken, sign.getId()))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining(TOKEN_EXPIRED.getMessage());
    }

    @Test
    void 회원_삭제() {
        // given
        String name = "홍길동";
        String email = "qwer1234@naver.com";
        String pwd = "qwer1234@A";
        Address address = new Address("서울시", "광진구", "능동로 141", "100-100");

        Member signUp = userServiceCommand.signUp(name, email, pwd, address);

        // when
        userServiceCommand.deleteMember(signUp.getId());

        // then
        assertThatThrownBy(() -> userServiceQuery.findMember(signUp.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }
}
