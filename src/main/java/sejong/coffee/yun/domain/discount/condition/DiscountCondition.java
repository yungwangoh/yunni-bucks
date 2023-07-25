package sejong.coffee.yun.domain.discount.condition;

import sejong.coffee.yun.domain.user.User;

public interface DiscountCondition {

    boolean isSatisfiedBy(User user);
}
