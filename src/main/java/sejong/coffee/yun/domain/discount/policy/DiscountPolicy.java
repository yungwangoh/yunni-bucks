package sejong.coffee.yun.domain.discount.policy;

import sejong.coffee.yun.domain.user.User;

public interface DiscountPolicy {

    double calculateDiscount(User user);
}
