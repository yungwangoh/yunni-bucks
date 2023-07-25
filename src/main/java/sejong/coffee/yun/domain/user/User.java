package sejong.coffee.yun.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.DateTimeEntity;
import sejong.coffee.yun.domain.coupon.Coupon;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends DateTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String email;
    @Column(name = "order_id")
    private Long orderId;
    @Enumerated(value = EnumType.STRING)
    private UserRank userRank;
    private Address address;
    private Money money;
    @OneToMany
    private List<Coupon> coupons = new ArrayList<>();

    @OneToOne
    private Card card;

    @Builder
    public User(String email, String name, String password, Long orderId,
                UserRank userRank, Address address, Money money, List<Coupon> coupons) {

        this.name = name;
        this.password = password;
        this.email = email;
        this.orderId = orderId;
        this.userRank = userRank;
        this.address = address;
        this.money = money;
        this.coupons = coupons;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
