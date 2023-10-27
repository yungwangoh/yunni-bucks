package sejong.coffee.yun.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.exception.CouponException;
import sejong.coffee.yun.domain.exception.ExceptionControl;
import sejong.coffee.yun.domain.user.Coupon;
import sejong.coffee.yun.domain.user.CouponUse;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.repository.coupon.CouponRepository;
import sejong.coffee.yun.repository.user.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    @Transactional
    public Coupon save(String name, String identityNumber, LocalDateTime createAt,
                       LocalDateTime expireAt, double discountRate, int quantity) {

        Coupon coupon = Coupon.builder()
                .quantity(quantity)
                .expireAt(expireAt)
                .discountRate(discountRate)
                .name(name)
                .createAt(createAt)
                .couponUse(CouponUse.NO)
                .identityNumber(identityNumber)
                .build();

        return couponRepository.save(coupon);
    }

    @Transactional
    public Coupon couponRegistry(Long couponId, Long memberId) {

        Coupon coupon = couponRepository.findById(couponId);
        Member member = userRepository.findById(memberId);

        if(member.hasCoupon()) throw new CouponException(ExceptionControl.ALREADY_EXIST_COUPON.getMessage());

        member.setCoupon(coupon);

        return coupon;
    }

    public Coupon findCoupon(Long couponId) {
        return couponRepository.findById(couponId);
    }

    @Transactional
    public void deleteCoupon(Coupon coupon) {
        couponRepository.delete(coupon);
    }

    @Transactional
    public void deleteCoupon(Long couponId) {
        couponRepository.deleteById(couponId);
    }
}
