package sejong.coffee.yun.domain.discount.condition;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import sejong.coffee.yun.domain.coupon.Coupon;
import sejong.coffee.yun.domain.user.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class CouponConditionTest {

    private final DiscountCondition discountCondition;

    public CouponConditionTest() {
        this.discountCondition = new CouponCondition();
    }

    @Test
    void 유저가_쿠폰을_가지고_있지_않다() {
        // given
        User user = User.builder()
                .coupons(new ArrayList<>())
                .build();

        // when
        boolean couponCondition = discountCondition.isSatisfiedBy(user);

        // then
        assertFalse(couponCondition);
    }

    @Test
    void 유저가_쿠폰을_가지고_있다() {
        // given
        Coupon coupon = new Coupon("첫 가입 쿠폰", "첫 가입한 사람한테만 주는 쿠폰", 0.1);
        List<Coupon> coupons = List.of(coupon);

        User user = User.builder()
                .coupons(coupons)
                .build();

        // when
        boolean couponCondition = discountCondition.isSatisfiedBy(user);

        // then
        assertTrue(couponCondition);
    }
}