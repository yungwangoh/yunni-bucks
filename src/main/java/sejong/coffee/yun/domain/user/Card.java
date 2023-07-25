package sejong.coffee.yun.domain.user;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of ={"id", "cardNumber", "validThru"})
public class Card {

    @Id @GeneratedValue
    @Column(name = "card_id")
    private Long id;
    private String cardPhoto;
    private String cardNumber;
    private String validThru;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

}
