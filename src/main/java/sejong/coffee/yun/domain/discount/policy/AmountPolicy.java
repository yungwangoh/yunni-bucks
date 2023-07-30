package sejong.coffee.yun.domain.discount.policy;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import sejong.coffee.yun.domain.discount.condition.DiscountCondition;
import sejong.coffee.yun.domain.user.Coupon;
import sejong.coffee.yun.domain.user.Member;

@RequiredArgsConstructor
public class AmountPolicy implements DiscountPolicy {

    private final DiscountCondition condition;

    @Override
    public double calculateDiscount(Member member) {
        if(!condition.isSatisfiedBy(member)) {
            return 1000;
        }
        return 0;
    }
}
