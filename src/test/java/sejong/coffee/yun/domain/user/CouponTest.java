package sejong.coffee.yun.domain.user;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import sejong.coffee.yun.domain.exception.CouponException;
import sejong.coffee.yun.domain.exception.ExceptionControl;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CouponTest {

    @Test
    void 쿠폰_식별번호_예외_확인() {
        assertThatThrownBy(() -> Coupon.builder()
                .identityNumber("12345-31243-543534")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("쿠폰 식별번호 형식에 맞지 않습니다.");

    }

    @Test
    void 쿠폰이_만료기간을_넘었을때_쿠폰사용_상태_변경_확인() {
        // given
        Coupon coupon = Coupon.builder()
                .identityNumber("1234-1234-1234-1234")
                .couponUse(CouponUse.NO)
                .createAt(LocalDateTime.of(2023, 7, 29, 3, 56))
                .expireAt(LocalDateTime.of(2023, 7, 30, 3, 56))
                .build();

        // when

        // then
        assertThatThrownBy(() -> coupon.checkExpireTime(LocalDateTime.of(2024, 7, 29,3, 56)))
                .isInstanceOf(CouponException.class)
                .hasMessageContaining(ExceptionControl.COUPON_OVER_EXPIRE_TIME.getMessage());

        assertThat(coupon.getCouponUse()).isEqualTo(CouponUse.YES);
    }

    @Test
    void 쿠폰_상태_변경() {
        // given
        Coupon coupon = Coupon.builder()
                .identityNumber("1234-1234-1234-1234")
                .couponUse(CouponUse.NO)
                .createAt(LocalDateTime.of(2023, 7, 29, 3, 56))
                .expireAt(LocalDateTime.of(2023, 7, 30, 3, 56))
                .build();

        // when
        coupon.convertStatusUsedCoupon();

        // then
        assertThat(coupon.getCouponUse()).isEqualTo(CouponUse.YES);
    }

    @Test
    void 쿠폰이_사용_가능한지_확인() {
        // given
        Coupon coupon = Coupon.builder()
                .identityNumber("1234-1234-1234-1234")
                .couponUse(CouponUse.NO)
                .createAt(LocalDateTime.of(2023, 7, 29, 3, 56))
                .expireAt(LocalDateTime.of(2023, 7, 30, 3, 56))
                .build();

        // when
        boolean availableCoupon = coupon.hasAvailableCoupon();

        // then
        assertTrue(availableCoupon);
    }

    @Test
    void 쿠폰이_사용_불가능한지_확인() {
        // given
        Coupon coupon = Coupon.builder()
                .identityNumber("1234-1234-1234-1234")
                .couponUse(CouponUse.YES)
                .createAt(LocalDateTime.of(2023, 7, 29, 3, 56))
                .expireAt(LocalDateTime.of(2023, 7, 30, 3, 56))
                .build();

        // when
        boolean availableCoupon = coupon.hasAvailableCoupon();

        // then
        assertFalse(availableCoupon);
    }

    @Test
    void 유저가_쿠폰을_등록하면_수량은_하나가_내려간다() {
        // given
        int quantity = 100;

        Coupon coupon = Coupon.builder()
                .identityNumber("1234-1234-1234-1234")
                .couponUse(CouponUse.YES)
                .createAt(LocalDateTime.of(2023, 7, 29, 3, 56))
                .expireAt(LocalDateTime.of(2023, 7, 30, 3, 56))
                .quantity(quantity)
                .build();

        Member member = Member.builder()
                .address(null)
                .orderCount(0)
                .email("qwer1234@naver.com")
                .money(Money.ZERO)
                .userRank(UserRank.BRONZE)
                .password("qwer1234")
                .name("윤광오")
                .coupon(coupon)
                .build();

        // when
        member.getCoupon().subQuantity();

        // then
        assertThat(member.getCoupon().getQuantity()).isEqualTo(quantity - 1);
    }
}