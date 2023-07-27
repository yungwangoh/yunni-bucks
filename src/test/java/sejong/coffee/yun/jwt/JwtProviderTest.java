package sejong.coffee.yun.jwt;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.domain.user.UserRank;
import sejong.coffee.yun.util.jwt.JwtUtil;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtProviderTest {

    private Member member;

    @BeforeEach
    void init() {
        member = Member.builder()
                .address(null)
                .email("qwer1234@naver.com")
                .name("홍길동")
                .money(Money.ZERO)
                .password("qwer1234@A")
                .orderId(1L)
                .userRank(UserRank.BRONZE)
                .build();
    }

    @Test
    void 토큰_만료_확인() {
        // given
        JwtProvider jwtProvider = new JwtProvider("key", 0L, 0L);

        // when
        String accessToken = jwtProvider.createAccessToken(member);
        String refreshToken = jwtProvider.createRefreshToken(member);

        // then
        assertFalse(jwtProvider.tokenExpiredCheck(accessToken));
        assertFalse(jwtProvider.tokenExpiredCheck(refreshToken));
    }

    @Test
    void 토큰이_만료되지_않음() {
        // given
        JwtProvider jwtProvider = new JwtProvider("key", 100000L, 1000000L);

        // when
        String accessToken = jwtProvider.createAccessToken(member);
        String refreshToken = jwtProvider.createRefreshToken(member);

        // then
        assertTrue(jwtProvider.tokenExpiredCheck(accessToken));
        assertTrue(jwtProvider.tokenExpiredCheck(refreshToken));
    }

    @Test
    void 토큰_추출() {
        // given
        JwtProvider jwtProvider = new JwtProvider("key", 100000L, 1000000L);
        Long memberId = 1L;

        Member m = Member.from(memberId, member);

        String accessToken = jwtProvider.createAccessToken(m);

        // when
        Long extractMemberId = jwtProvider.mapTokenToId(JwtUtil.getBearerToken(accessToken));

        // then
        assertThat(extractMemberId).isEqualTo(memberId);
    }
}