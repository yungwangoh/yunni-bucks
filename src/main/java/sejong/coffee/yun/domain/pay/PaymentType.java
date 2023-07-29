package sejong.coffee.yun.domain.pay;

import sejong.coffee.yun.domain.exception.NotFoundPaymentTypeException;

public enum PaymentType {
    CARD, ACCOUNT, COUPON;

    PaymentType() {
    }

    public static PaymentType designatePaymentType(String type) {
        if (type.equals("카드")) {
            return PaymentType.CARD;
        } else if (type.equals("계좌")) {
            return PaymentType.ACCOUNT;
        } else {
            throw new NotFoundPaymentTypeException();
        }
    }
}
