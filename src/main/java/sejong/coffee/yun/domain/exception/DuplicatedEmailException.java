package sejong.coffee.yun.domain.exception;

public class DuplicatedEmailException extends DuplicatedException {

    public DuplicatedEmailException() {
    }

    public DuplicatedEmailException(String s) {
        super(s);
    }

    public DuplicatedEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicatedEmailException(Throwable cause) {
        super(cause);
    }
}
