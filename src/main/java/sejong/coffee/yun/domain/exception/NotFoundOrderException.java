package sejong.coffee.yun.domain.exception;

public class NotFoundOrderException extends IllegalArgumentException{

    public NotFoundOrderException() {
    }

    public NotFoundOrderException(String s) {
        super(s);
    }

    public NotFoundOrderException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundOrderException(Throwable cause) {
        super(cause);
    }
}
