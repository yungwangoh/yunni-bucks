package sejong.coffee.yun.repository.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import sejong.coffee.yun.domain.user.Address;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.domain.user.User;
import sejong.coffee.yun.domain.user.UserRank;
import sejong.coffee.yun.repository.user.impl.UserRepositoryImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

//    public UserRepositoryTest() {
//        this.userRepository = new UserRepositoryImpl();
//    }

    private User user;

    @BeforeEach
    void init() {
        String city = "서울시";
        String district = "강서구";
        String detail = "목동";
        String zipcode = "123-123";

        Address address = new Address(city, district, detail, zipcode);

        user = User.builder()
                .address(address)
                .email("qwer1234@naver.com")
                .money(Money.ZERO)
                .userRank(UserRank.BRONZE)
                .password("qwer1234")
                .name("윤광오")
                .build();
    }

    @Test
    void 유저_저장() {
        // given

        // when
        User save = userRepository.save(user);

        // then
        assertThat(save.getId()).isEqualTo(1);
    }

    @Test
    void 유저_이름_변경() {
        // given
        String updateName = "하윤";
        User save = userRepository.save(user);

        // when
        userRepository.updateName(save.getId(), updateName);

        // then
        assertThat(save.getName()).isEqualTo(updateName);
    }

    @Test
    void 유저가_존재하는지_확인() {
        // given
        User save = userRepository.save(user);

        // when
        boolean exist = userRepository.exist(save.getId());

        // then
        assertTrue(exist);
    }
}