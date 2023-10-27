package sejong.coffee.yun.repository.coupon;

import sejong.coffee.yun.domain.user.Coupon;

public interface CouponRepository {

    Coupon save(Coupon coupon);
    Coupon findById(Long id);
    void delete(Coupon coupon);
    void deleteById(Long id);
}
