package sejong.coffee.yun.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.discount.type.DiscountType;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @Builder
    public Coupon(String name, String identityNumber, LocalDateTime createAt, LocalDateTime expireAt, double discountRate) {
        this.name = name;
        this.identityNumber = identityNumber;
        this.createAt = createAt;
        this.expireAt = expireAt;
        this.discountRate = discountRate;
    }

    @Override
    public double getDiscountRate() {
        return this.discountRate;
    }
}
