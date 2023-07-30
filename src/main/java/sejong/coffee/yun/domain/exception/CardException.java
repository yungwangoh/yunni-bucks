package sejong.coffee.yun.domain.exception;

import java.util.NoSuchElementException;

public class CardException extends NoSuchElementException {

    public CardException() {
    }

    public CardException(String s, Throwable cause) {
        super(s, cause);
    }

    public CardException(Throwable cause) {
        super(cause);
    }

    public CardException(String s) {
        super(s);
    }
}
