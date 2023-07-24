package sejong.coffee.yun.domain.discount.condition;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.domain.user.User;
import sejong.coffee.yun.domain.user.UserRank;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RankConditionTest {
    private final DiscountCondition rankCondition;

    public RankConditionTest() {
        this.rankCondition = new RankCondition();
    }

    @Test
    void 유저의_랭크가_조건에_불충족하는지_확인() {
        // given
        User user = User.builder()
                .money(Money.ZERO)
                .userRank(UserRank.BRONZE)
                .build();

        // when
        boolean condition = rankCondition.isSatisfiedBy(user);

        // then
        assertFalse(condition);
    }

    @ParameterizedTest
    @ValueSource(strings = {"SILVER", "GOLD", "PLATINUM", "DIAMOND"})
    void 유저의_랭크가_조건에_충족하는지_확인(UserRank userRank) {
        // given
        User user = User.builder()
                .money(Money.ZERO)
                .userRank(userRank)
                .build();

        // when
        boolean condition = rankCondition.isSatisfiedBy(user);

        // then
        assertTrue(condition);
    }
}