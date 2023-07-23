package sejong.coffee.yun.domain.discount.condition;

import sejong.coffee.yun.domain.user.User;
import sejong.coffee.yun.domain.user.UserRank;

import java.util.Objects;

public class RankCondition implements DiscountCondition {

    @Override
    public boolean isSatisfiedBy(User user) {
        return checkCondition(user.getUserRank(), UserRank.SILVER) || checkCondition(user.getUserRank(), UserRank.GOLD) ||
                checkCondition(user.getUserRank(), UserRank.PLATINUM) || checkCondition(user.getUserRank(), UserRank.DIAMOND);
    }

    private boolean checkCondition(UserRank userRank, UserRank conditionRank) {
        return Objects.equals(userRank, conditionRank);
    }
}
