package sejong.coffee.yun.domain.discount.policy;

import sejong.coffee.yun.domain.user.Coupon;
import sejong.coffee.yun.domain.user.Member;

public interface DiscountPolicy {

    double calculateDiscount(Member member);
}
