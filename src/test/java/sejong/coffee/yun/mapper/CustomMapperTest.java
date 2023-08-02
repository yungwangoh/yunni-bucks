package sejong.coffee.yun.mapper;

import org.junit.jupiter.api.Test;
import sejong.coffee.yun.domain.user.Address;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.domain.user.UserRank;
import sejong.coffee.yun.dto.user.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

class CustomMapperTest {

    private final CustomMapper customMapper;

    public CustomMapperTest() {
        this.customMapper = new CustomMapper();
    }

    @Test
    void mapper_map_테스트() {
        // given
        Address address = Address.builder()
                .city("서울시")
                .district("광진구")
                .detail("화양동")
                .zipCode("123-123")
                .build();

        Member member = Member.builder()
                .email("qwer1234@naver.com")
                .password("qwer1234@A")
                .name("홍길동")
                .userRank(UserRank.BRONZE)
                .money(Money.ZERO)
                .address(address)
                .build();

        // when
        UserDto.Sign.Up.Response response = customMapper.map(member, UserDto.Sign.Up.Response.class);

        // then
        assertThat(response.name()).isEqualTo(member.getName());
        assertThat(response.email()).isEqualTo(member.getEmail());
        assertThat(response.address()).isEqualTo(member.getAddress());
        assertThat(response.money()).isEqualTo(member.getMoney());
        assertThat(response.memberId()).isEqualTo(member.getId());
    }
}