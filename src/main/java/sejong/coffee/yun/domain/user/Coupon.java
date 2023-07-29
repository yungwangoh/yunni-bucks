package sejong.coffee.yun.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.discount.type.DiscountType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static sejong.coffee.yun.domain.user.CouponUse.*;

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

    @Builder
    public Coupon(String name, String identityNumber, LocalDateTime createAt,
                  LocalDateTime expireAt, double discountRate, CouponUse couponUse) {

        this.name = name;
        this.identityNumber = checkValidatedCouponIdentityNumber(identityNumber);
        this.createAt = createAt;
        this.expireAt = expireAt;
        this.discountRate = discountRate;
        this.couponUse = couponUse;
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
