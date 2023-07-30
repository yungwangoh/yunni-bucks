package sejong.coffee.yun.domain.exception;

public class NotMatchUserException extends IllegalArgumentException{
    public NotMatchUserException() {
    }

    public NotMatchUserException(String s) {
        super(s);
    }

    public NotMatchUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotMatchUserException(Throwable cause) {
        super(cause);
    }
}
