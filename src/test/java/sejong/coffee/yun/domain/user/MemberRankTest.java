package sejong.coffee.yun.domain.user;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class MemberRankTest {

    @ParameterizedTest
    @CsvSource({"0, BRONZE", "1, SILVER", "6, GOLD", "11, PLATINUM", "16, DIAMOND"})
    void 주문_개수_기준으로_등급_승급(int orderCount, UserRank rank) {
        // given
        Member member = Member.builder()
                .userRank(UserRank.calculateUserRank(orderCount))
                .build();

        // when
        UserRank userRank = member.getUserRank();

        // then
        assertThat(userRank).isEqualTo(rank);
    }
}