package sejong.coffee.yun.domain.discount.policy;

import lombok.RequiredArgsConstructor;
import sejong.coffee.yun.domain.discount.condition.DiscountCondition;
import sejong.coffee.yun.domain.user.User;

@RequiredArgsConstructor
public class AmountPolicy implements DiscountPolicy {

    private final DiscountCondition condition;

    @Override
    public double calculateDiscount(User user) {
        if(!condition.isSatisfiedBy(user)) {
            return 1000;
        }
        return 0;
    }
}
