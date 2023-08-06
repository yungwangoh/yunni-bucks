package sejong.coffee.yun.service;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import sejong.coffee.yun.domain.exception.NotFoundException;
import sejong.coffee.yun.domain.exception.NotMatchUserException;
import sejong.coffee.yun.domain.order.MenuList;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.order.menu.Beverage;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.order.menu.MenuSize;
import sejong.coffee.yun.domain.order.menu.Nutrients;
import sejong.coffee.yun.domain.user.Address;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.domain.user.UserRank;
import sejong.coffee.yun.jwt.JwtProvider;
import sejong.coffee.yun.repository.order.OrderRepository;
import sejong.coffee.yun.repository.redis.RedisRepository;
import sejong.coffee.yun.repository.user.UserRepository;
import sejong.coffee.yun.util.password.PasswordUtil;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_USER;
import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_MATCH_USER;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private RedisRepository redisRepository;

    private Member member;
    private Order order;

    @BeforeEach
    void init() {
        member = Member.builder()
                .address(new Address("서울시", "광진구", "화양동", "123-432"))
                .userRank(UserRank.BRONZE)
                .name("홍길동")
                .password("qwer1234@A")
                .money(Money.ZERO)
                .email("qwer123@naver.com")
                .build();

        Nutrients nutrients = new Nutrients(80, 80, 80, 80);
        Menu menu1 = new Beverage("커피", "에티오피아산 커피",
                Money.initialPrice(new BigDecimal(1000)), nutrients, MenuSize.M);

        order = Order.createOrder(member, new MenuList(List.of(menu1)), null);
    }

    @Test
    void 회원_가입() {
        // given
        given(userRepository.save(any())).willReturn(member);

        // when
        Member signUp = userService.signUp(member.getName(), member.getEmail(), member.getPassword(), member.getAddress());

        // then
        assertThat(signUp.getName()).isEqualTo(member.getName());
        assertThat(signUp.getEmail()).isEqualTo(member.getEmail());
        assertThat(signUp.getPassword()).isEqualTo(member.getPassword());
        assertThat(signUp.getAddress()).isEqualTo(member.getAddress());
    }

    @Test
    void 회원_찾기() {
        // given
        given(userRepository.findById(any())).willReturn(member);

        // when
        Member findMember = userService.findMember(member.getId());

        // then
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    void 회원_리스트() {
        // given
        given(userRepository.findAll()).willReturn(List.of(member));

        // when
        List<Member> list = userService.findAll();

        // then
        assertThat(list).isEqualTo(List.of(member));
    }

    @Test
    void 로그인() {
        // given
        String accessToken = "1234";
        String refreshToken = "1234";

        try(MockedStatic<PasswordUtil> passwordUtil = mockStatic(PasswordUtil.class)) {

            given(userRepository.findByEmail(any())).willReturn(member);
            given(jwtProvider.createAccessToken(any())).willReturn(accessToken);
            given(jwtProvider.createRefreshToken(any())).willReturn(refreshToken);
            given(PasswordUtil.match(anyString(), anyString())).willReturn(true);

            // when
            String token = userService.signIn(member.getEmail(), member.getPassword());

            // then
            assertThat(token).isEqualTo(accessToken);
        }
    }

    @Test
    void 로그아웃_만료된_토큰() {
        // given
        given(jwtProvider.tokenExpiredCheck(anyString())).willThrow(new JwtException("토큰이 만료되었습니다."));

        // when

        // then
        assertThatThrownBy(() -> userService.signOut("token", any()))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("토큰이 만료되었습니다.");
    }

    @Test
    void 로그인_입력정보와_가입정보와_다른_경우() {

        try(MockedStatic<PasswordUtil> passwordUtil = mockStatic(PasswordUtil.class)) {

            // given
            given(userRepository.findByEmail(any())).willThrow(NOT_FOUND_USER.notFoundException());
            given(PasswordUtil.match(anyString(), anyString())).willReturn(false);

            // when

            // then
            assertThatThrownBy(() -> userService.signIn(member.getEmail(), member.getPassword()))
                    .isInstanceOf(NotMatchUserException.class)
                    .hasMessageContaining(NOT_MATCH_USER.getMessage());
        }
    }

    @Test
    void 회원_이름_변경() {
        // given
        String updateName = "홍길동";

        given(userRepository.findById(any())).willReturn(member);

        // when
        Member updateMember = userService.updateName(1L, updateName);

        // then
        assertThat(updateMember.getName()).isEqualTo(updateName);
    }

    @Test
    void 회원_이메일_변경() {
        // given
        String updateEmail = "asdf1234@naver.com";

        given(userRepository.findById(any())).willReturn(member);

        // when
        Member updateMember = userService.updateEmail(1L, updateEmail);

        // then
        assertThat(updateMember.getEmail()).isEqualTo(updateEmail);
    }

    @Test
    void 회원_비밀번호_변경() {
        // given
        String updatePassword = "ghfsdjkhgs@A";

        given(userRepository.findById(any())).willReturn(member);

        // when
        Member updateMember = userService.updatePassword(1L, updatePassword);

        // then
        assertThat(updateMember.getPassword()).isEqualTo(updatePassword);
    }

    @Test
    void 회원_이름_변경_할때_다른_id를_넣은_경우() {
        // given
        given(userRepository.findById(any())).willThrow(NOT_FOUND_USER.notFoundException());

        // when

        // then
        assertThatThrownBy(() -> userService.updateName(any(), "gdgd"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    @Test
    void 회원_이메일_변경_할때_다른_id를_넣은_경우() {
        // given
        given(userRepository.findById(any())).willThrow(NOT_FOUND_USER.notFoundException());

        // when

        // then
        assertThatThrownBy(() -> userService.updateEmail(any(), "gdgd"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    @Test
    void 회원_패스워드_변경_할때_다른_id를_넣은_경우() {
        // given
        given(userRepository.findById(any())).willThrow(NOT_FOUND_USER.notFoundException());

        // when

        // then
        assertThatThrownBy(() -> userService.updatePassword(any(), "gdgd"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(NOT_FOUND_USER.getMessage());
    }

    @Test
    void 유저가_어떤_주문을_했는지_조회() {
        // given
        given(orderRepository.findAllByMemberId(any())).willReturn(List.of(order));

        // when
        List<Order> orders = userService.findAllByMemberId(any());

        // then
        assertThat(orders.size()).isEqualTo(1);
    }

    @Test
    void 로그아웃_할_때_엑세스_토큰이_만료된_경우() {
        // given
        given(jwtProvider.tokenExpiredCheck(anyString())).willThrow(new JwtException("토큰이 만료되었습니다."));

        // when

        // then
        assertThatThrownBy(() -> userService.signOut("token", any()))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("토큰이 만료되었습니다.");
    }

    @Test
    void 회원_삭제() {
        // given

        // when
        userService.deleteMember(1L);

        // then
        then(userRepository).should().delete(1L);
    }
}