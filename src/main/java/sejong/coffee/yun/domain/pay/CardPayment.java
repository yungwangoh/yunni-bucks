package sejong.coffee.yun.domain.pay;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sejong.coffee.yun.domain.order.Order;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of ={"id", "cardNumber", "cardPassword"})
public class CardPayment extends PaymentBaseEntity implements Pay {

    @Id @GeneratedValue
    @Column(name = "card_payment_id")
    private Long id;
    private String cardNumber;
    private String cardPassword;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    public CardPayment(String cardNumber, String cardPassword, Order order) {
        this.cardNumber = cardNumber;
        this.cardPassword = cardPassword;
        this.order = order;
    }

    @Override
    public void payment() {
        Pay pay = new CardPayment();
    }

    @Override
    public void cancelPayment() {

    }
}
