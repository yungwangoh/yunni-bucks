package sejong.coffee.yun.controller.pay;

import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.pay.BeforeCreatedData;
import sejong.coffee.yun.domain.pay.CardPayment;
import sejong.coffee.yun.domain.user.Card;
import sejong.coffee.yun.infra.fake.FakeUuidHolder;

class CreatePaymentData extends BeforeCreatedData {

    protected final CardPayment cardPayment;

    private Order getOrderByParentData() {
        return order;
    }

    public Card getCardByParentData() {
        return card;
    }

    public CreatePaymentData() {
        this.cardPayment = new CardPayment(getCardByParentData(), getOrderByParentData(), new FakeUuidHolder("asdfasdf"));
    }
}
