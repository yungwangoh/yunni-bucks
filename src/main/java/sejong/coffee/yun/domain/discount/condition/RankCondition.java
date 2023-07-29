package sejong.coffee.yun.domain.discount.condition;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import sejong.coffee.yun.domain.user.Coupon;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.UserRank;

import java.util.Objects;

public class RankCondition implements DiscountCondition {

    @Override
    public boolean isSatisfiedBy(Member member) {
        return checkCondition(member.getUserRank(), UserRank.SILVER) || checkCondition(member.getUserRank(), UserRank.GOLD) ||
                checkCondition(member.getUserRank(), UserRank.PLATINUM) || checkCondition(member.getUserRank(), UserRank.DIAMOND);
    }

    private boolean checkCondition(UserRank userRank, UserRank conditionRank) {
        return Objects.equals(userRank, conditionRank);
    }
}
