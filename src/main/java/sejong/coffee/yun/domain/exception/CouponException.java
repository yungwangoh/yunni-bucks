package sejong.coffee.yun.domain.exception;

public class CouponException extends IllegalArgumentException{

    public CouponException() {
    }

    public CouponException(String s) {
        super(s);
    }

    public CouponException(String message, Throwable cause) {
        super(message, cause);
    }

    public CouponException(Throwable cause) {
        super(cause);
    }
}
