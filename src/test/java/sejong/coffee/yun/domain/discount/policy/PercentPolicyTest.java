package sejong.coffee.yun.domain.discount.policy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import sejong.coffee.yun.domain.discount.condition.RankCondition;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.domain.user.UserRank;

import static org.assertj.core.api.Assertions.assertThat;

class PercentPolicyTest {
    private final DiscountPolicy discountPolicy;

    public PercentPolicyTest() {
        this.discountPolicy = new PercentPolicy(new RankCondition());
    }

    @ParameterizedTest
    @CsvSource({"BRONZE, 0", "SILVER, 0.1", "GOLD, 0.15", "PLATINUM, 0.2", "DIAMOND, 0.3"})
    void 할인_정책_등급_할인(UserRank userRank, double rankDiscountPercent) {
        // given
        Member member = Member.builder()
                .userRank(userRank)
                .money(Money.ZERO)
                .build();

        // when
        double percent = discountPolicy.calculateDiscount(member);

        // then
        assertThat(percent).isEqualTo(rankDiscountPercent);
    }
}