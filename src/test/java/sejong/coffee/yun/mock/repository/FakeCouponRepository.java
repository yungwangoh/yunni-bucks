package sejong.coffee.yun.mock.repository;

import org.springframework.stereotype.Repository;
import sejong.coffee.yun.domain.user.Coupon;
import sejong.coffee.yun.repository.coupon.CouponRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_COUPON;

@Repository
public class FakeCouponRepository implements CouponRepository {

    private final List<Coupon> coupons = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong id = new AtomicLong(0);

    @Override
    public Coupon save(Coupon coupon) {
        if(coupon.getId() == null || coupon.getId() == 0L) {
            Coupon newCoupon = Coupon.from(id.incrementAndGet(), coupon);

            coupons.add(newCoupon);
            return newCoupon;
        }
        coupons.removeIf(element -> Objects.equals(element, coupon));
        coupons.add(coupon);

        return coupon;
    }

    @Override
    public Coupon findById(Long id) {
        return coupons.stream().filter(coupon -> Objects.equals(coupon.getId(), id))
                .findAny()
                .orElseThrow(NOT_FOUND_COUPON::notFoundException);
    }

    @Override
    public void delete(Coupon coupon) {
        coupons.removeIf(element -> Objects.equals(element, coupon));
    }

    @Override
    public void deleteById(Long id) {
        coupons.removeIf(element -> Objects.equals(element.getId(), id));
    }

    @Override
    public void clear() {
        coupons.clear();
    }
}
