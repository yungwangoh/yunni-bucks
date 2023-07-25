package sejong.coffee.yun.domain.exception;

public class NotFoundUserException extends IllegalArgumentException {

    public NotFoundUserException() {
    }

    public NotFoundUserException(String s) {
        super(s);
    }

    public NotFoundUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundUserException(Throwable cause) {
        super(cause);
    }
}
