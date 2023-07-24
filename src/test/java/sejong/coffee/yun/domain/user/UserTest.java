package sejong.coffee.yun.domain.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void 유저의_주소() {
        // given
        String city = "서울시";
        String district = "강서구";
        String detail = "목동";
        String zipcode = "123-123";

        Address address = new Address(city, district, detail, zipcode);

        User user = User.builder()
                .address(address)
                .email("qwer1234@naver.com")
                .money(Money.ZERO)
                .userRank(UserRank.BRONZE)
                .password("qwer1234")
                .name("윤광오")
                .build();

        // when
        Address userAddress = user.getAddress();

        // then
        assertThat(userAddress).isEqualTo(address);
    }

    @Test
    void 유저의_처음_잔액은_0이다() {
        // given
        User user = User.builder()
                .address(null)
                .email("qwer1234@naver.com")
                .money(Money.ZERO)
                .userRank(UserRank.BRONZE)
                .password("qwer1234")
                .name("윤광오")
                .build();

        // when
        Money userMoney = user.getMoney();

        // then
        assertThat(userMoney.getTotalPrice()).isEqualTo(Money.ZERO.getTotalPrice());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "100", "1000", "10000", "100000"})
    void 유저의_잔액_확인(String money) {
        // given
        User user = User.builder()
                .address(null)
                .email("qwer1234@naver.com")
                .money(Money.initialPrice(new BigDecimal(money)))
                .userRank(UserRank.BRONZE)
                .password("qwer1234")
                .name("윤광오")
                .build();

        // when
        Money userMoney = user.getMoney();

        // then
        assertThat(userMoney.getTotalPrice()).isEqualTo(Money.initialPrice(new BigDecimal(money)).getTotalPrice());
    }

    @ParameterizedTest
    @ValueSource(strings = {"BRONZE", "SILVER", "GOLD", "PLATINUM", "DIAMOND"})
    void 유저의_랭크(UserRank userRank) {
        // given
        User user = User.builder()
                .address(null)
                .email("qwer1234@naver.com")
                .money(Money.ZERO)
                .userRank(userRank)
                .password("qwer1234")
                .name("윤광오")
                .build();

        // when
        UserRank rank = user.getUserRank();

        // then
        assertThat(rank).isEqualTo(userRank);
    }
}