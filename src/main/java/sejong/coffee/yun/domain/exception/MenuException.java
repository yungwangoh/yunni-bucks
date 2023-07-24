package sejong.coffee.yun.domain.exception;

public class MenuException extends IllegalArgumentException {

    public MenuException() {
    }

    public MenuException(String s) {
        super(s);
    }

    public MenuException(String message, Throwable cause) {
        super(message, cause);
    }

    public MenuException(Throwable cause) {
        super(cause);
    }
}
