package sejong.coffee.yun.domain.user;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of ={"id", "cardNumber", "validThru"})
public class Card {

    @Id @GeneratedValue
    @Column(name = "card_id")
    private Long id;
    @Column(length = 20)
    @Length(max = 20)
    private String cardNumber;
    @Column(length = 4)
    private String cardPassword;
    private String validThru;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Member member;

    @Builder
    public Card(String cardNumber, String validThru, String cardPassword) {
        this.cardNumber = cardNumber;
        this.validThru = validThru;
        this.cardPassword = cardPassword;
    }
}
