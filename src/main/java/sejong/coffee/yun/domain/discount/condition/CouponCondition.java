package sejong.coffee.yun.domain.discount.condition;

import sejong.coffee.yun.domain.coupon.Coupon;
import sejong.coffee.yun.domain.user.User;

public class CouponCondition implements DiscountCondition {

    @Override
    public boolean isSatisfiedBy(User user) {
        return user.getCoupons().size() > 0;
    }
}
