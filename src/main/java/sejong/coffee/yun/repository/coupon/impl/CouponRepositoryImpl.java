package sejong.coffee.yun.repository.coupon.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.user.Coupon;
import sejong.coffee.yun.repository.coupon.CouponRepository;
import sejong.coffee.yun.repository.coupon.jpa.JpaCouponRepository;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_COUPON;

@Repository
@Primary
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final JpaCouponRepository jpaCouponRepository;

    @Override
    @Transactional
    public Coupon save(Coupon coupon) {
        return jpaCouponRepository.save(coupon);
    }

    @Override
    public Coupon findById(Long id) {
        return jpaCouponRepository.findById(id)
                .orElseThrow(NOT_FOUND_COUPON::notFoundException);
    }

    @Override
    @Transactional
    public void delete(Coupon coupon) {
        jpaCouponRepository.delete(coupon);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jpaCouponRepository.deleteById(id);
    }
}
