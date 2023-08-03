package sejong.coffee.yun.domain.exception;

public class DuplicatedNameException extends DuplicatedException {

    public DuplicatedNameException() {
    }

    public DuplicatedNameException(String s) {
        super(s);
    }

    public DuplicatedNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicatedNameException(Throwable cause) {
        super(cause);
    }
}
