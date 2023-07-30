package sejong.coffee.yun.domain.exception;

import java.util.NoSuchElementException;

public class PaymentDetailsException extends NoSuchElementException {

    public PaymentDetailsException() {
    }

    public PaymentDetailsException(String s, Throwable cause) {
        super(s, cause);
    }

    public PaymentDetailsException(Throwable cause) {
        super(cause);
    }

    public PaymentDetailsException(String s) {
        super(s);
    }
}
