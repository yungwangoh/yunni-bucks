package sejong.coffee.yun.repository.coupon.jpa;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import sejong.coffee.yun.domain.user.Coupon;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface JpaCouponRepository extends JpaRepository<Coupon, Long> {

    @NotNull
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Coupon> findById(@NotNull Long couponId);
}
