package sejong.coffee.yun.domain.exception;

import lombok.Getter;

@Getter
public enum ExceptionControl {
    EMPTY_MENUS("메뉴리스트가 비어 있습니다.");

    private final String message;

    ExceptionControl(String message) {
        this.message = message;
    }

    public MenuException throwException() {
        return new MenuException(this.message);
    }
}
