package sejong.coffee.yun.domain.exception;

import java.util.NoSuchElementException;

public class NotFoundPaymentTypeException extends NoSuchElementException {

    public NotFoundPaymentTypeException() {
    }

    public NotFoundPaymentTypeException(String s, Throwable cause) {
        super(s, cause);
    }

    public NotFoundPaymentTypeException(Throwable cause) {
        super(cause);
    }

    public NotFoundPaymentTypeException(String s) {
        super(s);
    }
}
