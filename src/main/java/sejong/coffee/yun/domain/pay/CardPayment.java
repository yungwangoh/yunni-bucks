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

    // 카드번호, 카드 유효연월은 Member에서 끌고오는지?
    // Member랑 연관관계 맺나?

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Override
    public void payment() {

    }

    @Override
    public void cancelPayment() {

    }
}
