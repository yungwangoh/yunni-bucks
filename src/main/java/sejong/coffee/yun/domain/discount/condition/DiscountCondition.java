package sejong.coffee.yun.domain.discount.condition;

import sejong.coffee.yun.domain.user.Member;

public interface DiscountCondition {

    boolean isSatisfiedBy(Member member);
}
