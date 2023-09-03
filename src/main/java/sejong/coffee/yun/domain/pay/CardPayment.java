package sejong.coffee.yun.domain.pay;

import lombok.*;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.user.Card;
import sejong.coffee.yun.dto.pay.CardPaymentDto;
import sejong.coffee.yun.infra.port.UuidHolder;
import sejong.coffee.yun.util.parse.ParsingUtil;

import javax.persistence.*;
import java.time.LocalDateTime;

import static sejong.coffee.yun.domain.pay.PaymentStatus.CANCEL;
import static sejong.coffee.yun.domain.pay.PaymentStatus.DONE;
import static sejong.coffee.yun.util.parse.ParsingUtil.parsingCardValidDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@ToString(of = {"id", "cardNumber", "cardPassword", "customerName", "cardExpirationYear", "cardExpirationMonth",
//        "paymentKey", "orderId", "requestedAt", "approvedAt", "paymentStatus"})
@ToString
@Table(name = "card_payment")
public class CardPayment extends PaymentDateTimeEntity implements Pay {

    @Id
    @GeneratedValue
    @Column(name = "card_payment_id")
    private Long id;
    private String cardNumber;
    private String cardPassword;
    private String customerName;
    private String cardExpirationYear;
    private String cardExpirationMonth;
    private String paymentKey;
    private String orderUuid;
    private PaymentType paymentType;
    private PaymentStatus paymentStatus;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime cancelPaymentAt;
    private PaymentCancelReason cancelReason;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    public CardPayment(Card card, Order order, UuidHolder uuidHolder) {
        this.cardNumber = card.getNumber();
        this.cardPassword = displayTwoDigits(card.getCardPassword());
        this.customerName = order.getMember().getName();
        this.cardExpirationYear = parsingCardValidDate(card.getValidThru())[0];
        this.cardExpirationMonth = parsingCardValidDate(card.getValidThru())[1];
        this.order = order;
        this.paymentType = PaymentType.CARD;
        this.paymentStatus = DONE;
        this.orderUuid = uuidHolder.random();
    }

    @Builder
    public CardPayment(Long id, String cardNumber, String cardPassword, String customerName,
                       String cardExpirationYear, String cardExpirationMonth, PaymentType type,
                       PaymentStatus status, LocalDateTime requestedAt, LocalDateTime approvedAt,
                       String paymentKey, String orderUuid, Order order) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.cardPassword = displayTwoDigits(cardPassword);
        this.customerName = customerName;
        this.cardExpirationYear = cardExpirationYear;
        this.cardExpirationMonth = cardExpirationMonth;
        this.paymentType = type;
        this.paymentStatus = status;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.paymentKey = paymentKey;
        this.orderUuid = orderUuid;
        this.order = order;
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

    public static CardPayment fromModel(CardPaymentDto.Request cardDomain) {
        CardPayment cardPayment = new CardPayment();
        cardPayment.cardNumber = cardDomain.cardNumber();
        cardPayment.cardPassword = cardDomain.cardPassword();
        cardPayment.customerName = cardDomain.customerName();
        cardPayment.cardExpirationYear = cardDomain.cardExpirationYear();
        cardPayment.cardExpirationMonth = cardDomain.cardExpirationMonth();
        cardPayment.orderUuid = cardDomain.orderId();
        cardPayment.requestedAt = cardDomain.requestedAt();
        cardPayment.order = cardDomain.order();
        return cardPayment;
    }

    public static CardPayment approvalPayment(CardPayment cardPayment, String paymentKey,
                                              String approvedAt) {
        return CardPayment.builder()
                .cardNumber(cardPayment.getCardNumber())
                .cardExpirationYear(cardPayment.getCardExpirationYear())
                .cardExpirationMonth(cardPayment.getCardExpirationMonth())
                .type(cardPayment.getPaymentType())
                .cardPassword(cardPayment.getCardPassword())
                .customerName(cardPayment.getCustomerName())
                .requestedAt(cardPayment.getRequestedAt())
                .status(DONE)
                .paymentKey(paymentKey)
                .approvedAt(ParsingUtil.parsingISO8601ToLocalDateTime(approvedAt))
                .order(cardPayment.getOrder())
                .orderUuid(cardPayment.getOrderUuid())
                .build();
    }

    public void cancel(PaymentCancelReason cancelReason) {
        this.cancelReason = cancelReason;
        this.paymentStatus = CANCEL;
        this.cancelPaymentAt = LocalDateTime.now();
    }
}
