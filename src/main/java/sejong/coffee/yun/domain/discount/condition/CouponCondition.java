package sejong.coffee.yun.domain.discount.condition;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import sejong.coffee.yun.domain.user.Member;

public class CouponCondition implements DiscountCondition {

    @Override
    public boolean isSatisfiedBy(Member member) {
        return false;
    }
}
