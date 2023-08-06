package sejong.coffee.yun.domain.discount.policy;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import sejong.coffee.yun.domain.discount.condition.CouponCondition;
import sejong.coffee.yun.domain.discount.condition.DiscountCondition;
import sejong.coffee.yun.domain.discount.condition.RankCondition;
import sejong.coffee.yun.domain.discount.type.DiscountType;
import sejong.coffee.yun.domain.user.Member;

import java.util.Arrays;
import java.util.List;

@Component
@Primary
public class PercentPolicy implements DiscountPolicy {

    private final List<DiscountCondition> conditions;

    public PercentPolicy(DiscountCondition... conditions) {
        this.conditions = Arrays.asList(conditions);
    }

    @Override
    public double calculateDiscount(Member member) {

        double totalRate = 0d;

        if(isDiscountable(RankCondition.class, member)) {
            totalRate += provideDiscountRate(RankCondition.class, member.getUserRank());
        }

        if(isDiscountable(CouponCondition.class, member)) {
            totalRate += provideDiscountRate(CouponCondition.class, member.getCoupon());
        }

        return totalRate;
    }

    private boolean isDiscountable(Class<? extends DiscountCondition> conditionType, Member member) {
        return conditions.stream()
                .filter(conditionType::isInstance)
                .anyMatch(condition -> condition.isSatisfiedBy(member));
    }

    private double provideDiscountRate(Class<? extends DiscountCondition> conditionType, DiscountType discountType) {
        return conditions.stream()
                .filter(conditionType::isInstance)
                .mapToDouble(condition -> provideDiscountRateBy(discountType))
                .sum();
    }

    private double provideDiscountRateBy(DiscountType discountType) {
        return discountType.getDiscountRate();
    }
}
