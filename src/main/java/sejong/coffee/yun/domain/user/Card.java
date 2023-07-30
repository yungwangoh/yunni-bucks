package sejong.coffee.yun.domain.user;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import sejong.coffee.yun.domain.exception.ExceptionControl;

import javax.persistence.*;
import java.time.LocalDateTime;

import static sejong.coffee.yun.util.parse.ParsingDateUtil.parsingCardValidDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "cardNumber", "validThru"})
public class Card {

    @Id
    @GeneratedValue
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
    public Card(String cardNumber, String validThru, String cardPassword, Member member) {
        this.cardNumber = cardNumber;
        this.validThru = checkExpirationDate(validThru);
        this.cardPassword = cardPassword;
        this.member = member;
    }

    public String checkExpirationDate(String dateTime) {
        String[] splitDateTime = parsingCardValidDate(dateTime);
        int year = Integer.parseInt(splitDateTime[0]);
        int month = Integer.parseInt(splitDateTime[1]);
        if ((year < LocalDateTime.now().getYear() % 100) && (month > 12 || month < 0)) {
            throw ExceptionControl.INVALID_CARD_EXPIRATION_DATE.cardException();
        }
        return dateTime;
    }
}
