package sejong.coffee.yun.service.fake;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import sejong.coffee.yun.domain.exception.CouponException;
import sejong.coffee.yun.domain.exception.ExceptionControl;
import sejong.coffee.yun.domain.user.*;
import sejong.coffee.yun.jwt.JwtProvider;
import sejong.coffee.yun.mock.repository.FakeCouponRepository;
import sejong.coffee.yun.mock.repository.FakeUserRepository;
import sejong.coffee.yun.repository.coupon.CouponRepository;
import sejong.coffee.yun.repository.user.UserRepository;
import sejong.coffee.yun.service.CouponService;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringJUnitConfig
@ContextConfiguration(classes = {
        CouponService.class,
        FakeCouponRepository.class,
        FakeUserRepository.class,
        JwtProvider.class,
})
@TestPropertySource(properties = {
        "jwt.key=applicationKey",
        "jwt.expireTime.access=100000",
        "jwt.expireTime.refresh=1000000",
        "jwt.blackList=blackList",
        "schedules.cron.product=0/1 * * * * * "
})
public class CouponServiceTest {

    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private FakeUserRepository fakeUserRepository;
    @Autowired
    private FakeCouponRepository fakeCouponRepository;
    @Autowired
    private UserRepository userRepository;

    private Member member;
    private Coupon coupon;

    @BeforeEach
    void init() {
        coupon = Coupon.builder()
                .name("신규가입 쿠폰")
                .identityNumber("1234-1234-1234-1234")
                .couponUse(CouponUse.YES)
                .createAt(LocalDateTime.of(2023, 7, 29, 3, 56))
                .expireAt(LocalDateTime.of(2023, 7, 30, 3, 56))
                .quantity(100)
                .discountRate(0.1)
                .build();

        member = Member.builder()
                .name("윤광오")
                .userRank(UserRank.BRONZE)
                .money(Money.ZERO)
                .password("qwer1234")
                .address(null)
                .email("qwer1234@naver.com")
                .orderCount(0)
                .coupon(null)
                .build();
    }

    @AfterEach
    void initDB() {
        fakeUserRepository.clear();
        fakeCouponRepository.clear();
    }

    @Test
    void 유저가_쿠폰을_등록한다() {
        // given
        Member saveMember = userRepository.save(member);
        Coupon saveCoupon = couponRepository.save(coupon);

        // when
        Coupon registry = couponService.couponRegistry(saveCoupon.getId(), saveMember.getId(), LocalDateTime.of(2023, 7, 29, 3, 56));

        // then
        assertThat(registry.getQuantity()).isEqualTo(99);
        assertThat(saveMember.getCoupon()).isEqualTo(registry);
    }

    @Test
    void 쿠폰_수량이_0일_경우_예외() {
        // given
        Coupon coupon = Coupon.builder()
                .name("신규가입 쿠폰")
                .identityNumber("1234-1234-1234-1234")
                .couponUse(CouponUse.YES)
                .createAt(LocalDateTime.of(2023, 7, 29, 3, 56))
                .expireAt(LocalDateTime.of(2023, 7, 30, 3, 56))
                .quantity(0)
                .discountRate(0.1)
                .build();

        // when
        Member saveMember = userRepository.save(member);
        Coupon saveCoupon = couponRepository.save(coupon);

        // then
        assertThatThrownBy(() -> couponService.couponRegistry(saveCoupon.getId(), saveMember.getId(), LocalDateTime.of(2023, 7, 29, 3, 56)))
                .isInstanceOf(CouponException.class)
                .hasMessageContaining(ExceptionControl.COUPON_NOT_ENOUGH_QUANTITY.getMessage());
    }

    @Test
    void 유저가_쿠폰을_이미_가지고_있는_경우() {
        // given
        Member saveMember = userRepository.save(member);
        Coupon saveCoupon = couponRepository.save(coupon);

        // when
        couponService.couponRegistry(saveCoupon.getId(), saveMember.getId(), LocalDateTime.of(2023, 7, 29, 3, 56));

        // then
        assertThatThrownBy(() -> couponService.couponRegistry(saveCoupon.getId(), saveMember.getId(), LocalDateTime.of(2023, 7, 29, 3, 56)))
                .isInstanceOf(CouponException.class)
                .hasMessageContaining(ExceptionControl.ALREADY_EXIST_COUPON.getMessage());
    }

    @Test
    void 쿠폰의_유효기간이_넘은_경우_등록할_수_없다() {
        // given
        Member saveMember = userRepository.save(member);
        Coupon saveCoupon = couponRepository.save(coupon);

        // when

        // then
        assertThatThrownBy(() -> couponService.couponRegistry(saveCoupon.getId(), saveMember.getId(), LocalDateTime.now()))
                .isInstanceOf(CouponException.class)
                .hasMessageContaining(ExceptionControl.COUPON_OVER_EXPIRE_TIME.getMessage());
    }
}
