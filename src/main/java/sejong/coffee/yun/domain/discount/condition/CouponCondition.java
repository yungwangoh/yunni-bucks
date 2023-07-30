package sejong.coffee.yun.domain.discount.condition;

import sejong.coffee.yun.domain.user.Member;

public class CouponCondition implements DiscountCondition {

    @Override
    public boolean isSatisfiedBy(Member member) {
        return member.hasCoupon();
    }
}
