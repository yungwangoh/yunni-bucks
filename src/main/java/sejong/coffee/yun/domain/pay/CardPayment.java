package sejong.coffee.yun.domain.pay;

import lombok.*;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.user.Card;

import javax.persistence.*;

import static sejong.coffee.yun.domain.pay.PaymentStatus.READY;
import static sejong.coffee.yun.util.parse.ParsingDateTimeUtil.parsingCardValidDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of ={"id", "cardNumber", "cardPassword"})
//@JsonIgnoreProperties(ignoreUnknown = true)
public class CardPayment extends PaymentDateTimeEntity implements Pay {

    @Id @GeneratedValue
    @Column(name = "card_payment_id")
    private Long id;
    private String cardNumber;
    private String cardPassword;
    private String customerName;
    private String cardExpirationYear;
    private String cardExpirationMonth;
    private String paymentKey;
    private PaymentType paymentType;
    private PaymentStatus paymentStatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Builder
    public CardPayment(Card card, Order order) {
        this.cardNumber = card.getNumber();
        this.cardPassword = displayTwoDigits(card.getCardPassword());
        this.customerName = order.getMember().getName();
        this.cardExpirationYear = parsingCardValidDate(card.getValidThru())[0];
        this.cardExpirationMonth = parsingCardValidDate(card.getValidThru())[1];
        this.order = order;
        this.paymentStatus = READY;
    }

    @Override
    public void payment() {
        Pay pay = new CardPayment();
    }

    @Override
    public void cancelPayment() {

    }

    public String displayTwoDigits(String carPassword) {
        return carPassword.substring(0, 2);
    }


    public void update() {
        this.paymentStatus = PaymentStatus.DONE;
    }
}
