package sejong.coffee.yun.domain.discount.condition;

import org.springframework.stereotype.Component;
import sejong.coffee.yun.domain.user.Member;

@Component
public class CouponCondition implements DiscountCondition {

    @Override
    public boolean isSatisfiedBy(Member member) {
        return member.hasCoupon();
    }
}
