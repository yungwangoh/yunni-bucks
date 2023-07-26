package sejong.coffee.yun.domain.coupon;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.DateTimeEntity;
import sejong.coffee.yun.domain.user.User;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends DateTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private double discountRate;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public Coupon(String name, String description, double discountRate) {
        this.name = name;
        this.description = description;
        this.discountRate = discountRate;
    }
}
