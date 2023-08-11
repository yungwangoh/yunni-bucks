package sejong.coffee.yun.domain.exception;

import java.util.NoSuchElementException;

public class OcrException extends NoSuchElementException {

    public OcrException() {
    }

    public OcrException(String s, Throwable cause) {
        super(s, cause);
    }

    public OcrException(Throwable cause) {
        super(cause);
    }

    public OcrException(String s) {
        super(s);
    }
}
