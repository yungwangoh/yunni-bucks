package sejong.coffee.yun.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.user.Coupon;
import sejong.coffee.yun.repository.coupon.CouponRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CouponServiceQuery {

    private final CouponRepository couponRepository;

    public Coupon findCoupon(Long couponId) {
        return couponRepository.findById(couponId);
    }
}
