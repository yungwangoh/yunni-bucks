package sejong.coffee.yun.domain.exception;

import java.util.NoSuchElementException;

public class PaymentException extends NoSuchElementException {

    public PaymentException() {
    }

    public PaymentException(String s, Throwable cause) {
        super(s, cause);
    }

    public PaymentException(Throwable cause) {
        super(cause);
    }

    public PaymentException(String s) {
        super(s);
    }
}
