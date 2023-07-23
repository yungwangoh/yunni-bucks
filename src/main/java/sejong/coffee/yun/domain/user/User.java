package sejong.coffee.yun.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.DateTimeEntity;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends DateTimeEntity {

    @Id @GeneratedValue
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

    @Builder
    public User(String email, String name, String password, Long orderId, UserRank userRank, Address address) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.orderId = orderId;
        this.userRank = userRank;
        this.address = address;
    }
}
