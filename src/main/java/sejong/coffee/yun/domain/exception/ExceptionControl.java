package sejong.coffee.yun.domain.exception;

import lombok.Getter;

@Getter
public enum ExceptionControl {

    NOT_MATCH_USER("아이디 혹은 비밀번호가 다릅니다."),
    NOT_FOUND_ORDER("주문 내역이 존재하지 않습니다."),
    NOT_FOUND_USER("유저가 존재하지 않습니다."),
    EMPTY_MENUS("메뉴리스트가 비어 있습니다.");

    private final String message;

    ExceptionControl(String message) {
        this.message = message;
    }

    public MenuException throwException() {
        return new MenuException(this.message);
    }
    public NotFoundUserException notFoundUserException() {
        return new NotFoundUserException(this.message);
    }

    public NotFoundOrderException notFoundOrderException() {
        return new NotFoundOrderException(this.message);
    }
    public NotMatchUserException notMatchUserException() {
        return new NotMatchUserException(this.message);
    }
}
