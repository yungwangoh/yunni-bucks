package sejong.coffee.yun.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.discount.type.DiscountType;
import sejong.coffee.yun.domain.exception.CouponException;

import javax.persistence.*;
import java.time.LocalDateTime;

import static sejong.coffee.yun.domain.exception.ExceptionControl.COUPON_NOT_ENOUGH_QUANTITY;
import static sejong.coffee.yun.domain.user.CouponUse.NO;
import static sejong.coffee.yun.domain.user.CouponUse.YES;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon implements DiscountType {

    @Id @GeneratedValue
    private Long id;
    @Column(name = "coupon_name")
    private String name;
    @Column(name = "identity_number")
    private String identityNumber;
    @Column(name = "create_at")
    private LocalDateTime createAt;
    @Column(name = "expire_at")
    private LocalDateTime expireAt;
    @Column(name = "discount_rate")
    private double discountRate;
    @Enumerated(EnumType.STRING)
    private CouponUse couponUse;
    @Column(name = "quantity")
    private int quantity;

    @Builder
    public Coupon(Long id, String name, String identityNumber, LocalDateTime createAt, LocalDateTime expireAt, double discountRate, CouponUse couponUse, int quantity) {
        this.id = id;
        this.name = name;
        this.identityNumber = checkValidatedCouponIdentityNumber(identityNumber);
        this.createAt = createAt;
        this.expireAt = expireAt;
        this.discountRate = discountRate;
        this.couponUse = couponUse;
        this.quantity = quantity;
    }

    public static Coupon from(Long id, Coupon coupon) {
        return Coupon.builder()
                .id(id)
                .identityNumber(coupon.getIdentityNumber())
                .createAt(coupon.getCreateAt())
                .couponUse(coupon.getCouponUse())
                .expireAt(coupon.getExpireAt())
                .name(coupon.getName())
                .discountRate(coupon.getDiscountRate())
                .build();
    }

    @Override
    public double getDiscountRate() {
        return this.discountRate;
    }

    public void convertStatusUsedCoupon() {
        this.couponUse = YES;
    }

    public void checkExpireTime(LocalDateTime localDateTime) {
        if(localDateTime.compareTo(this.expireAt) > 0) {
            this.couponUse = YES;
        }
    }

    public void subQuantity() {
        if(this.quantity < 0) throw new CouponException(COUPON_NOT_ENOUGH_QUANTITY.getMessage());

        this.quantity--;
    }

    public boolean hasAvailableCoupon() {
        return this.getCouponUse() == NO;
    }

    private String checkValidatedCouponIdentityNumber(String identityNumber) {
        boolean matches = identityNumber.matches("^\\d{4}-\\d{4}-\\d{4}-\\d{4}$");

        if(matches) {
            return identityNumber;
        } else {
            throw new IllegalArgumentException("쿠폰 식별번호 형식에 맞지 않습니다.");
        }
    }
}
