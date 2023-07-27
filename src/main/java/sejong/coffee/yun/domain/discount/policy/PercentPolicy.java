package sejong.coffee.yun.domain.discount.policy;

import lombok.RequiredArgsConstructor;
import sejong.coffee.yun.domain.discount.condition.DiscountCondition;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.UserRank;

import java.util.Objects;

@RequiredArgsConstructor
public class PercentPolicy implements DiscountPolicy {

    private final DiscountCondition condition;

    @Override
    public double calculateDiscount(Member member) {
        if(condition.isSatisfiedBy(member)) {
            return calculateDiscountByUserRank(member.getUserRank());
        }
        return 0;
    }

    private double calculateDiscountByUserRank(UserRank userRank) {

        if(Objects.equals(userRank, UserRank.SILVER)) {
            return 0.1;
        } else if(Objects.equals(userRank, UserRank.GOLD)) {
            return 0.15;
        } else if(Objects.equals(userRank, UserRank.PLATINUM)) {
            return 0.2;
        } else if(Objects.equals(userRank, UserRank.DIAMOND)) {
            return 0.3;
        } else {
            throw new IllegalArgumentException("유저 랭크가 만족하지 않습니다.");
        }
    }
}
