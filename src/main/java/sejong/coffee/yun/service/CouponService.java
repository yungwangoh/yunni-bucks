package sejong.coffee.yun.service;

import sejong.coffee.yun.domain.user.Coupon;

import java.time.LocalDateTime;

public interface CouponService {

    default Coupon save(String name, String identityNumber, LocalDateTime createAt,
                       LocalDateTime expireAt, double discountRate, int quantity) {
        return null;
    }
    default Coupon couponRegistry(Long couponId, Long memberId, LocalDateTime localDateTime) {return null;}
    default void deleteCoupon(Coupon coupon) {}
    default void deleteCoupon(Long couponId) {}
    default Coupon findCoupon(Long couponId) {
        return null;
    }
}
