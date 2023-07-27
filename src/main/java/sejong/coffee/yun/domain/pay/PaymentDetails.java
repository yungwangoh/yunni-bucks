package sejong.coffee.yun.domain.pay;

import sejong.coffee.yun.domain.order.Order;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class PaymentDetails extends PaymentBaseEntity {
    private Long id;
    private int totalAmount; // 총 결제금액
    private int balanceAmount; // 취소할 수 있는 금액
    private int suppliedAmount; //공급가액
    private int vat; //부가세
    private Order order;
    private Pay pay;

    @Enumerated(value = EnumType.STRING)
    private PaymentType paymentType;

    /**비즈니스 로직
    * 세금 계산
     **/
}
