package sejong.coffee.yun.message;

import lombok.Getter;

@Getter
public enum SuccessOrFailMessage {

    SUCCESS_DUPLICATE_NAME("이름 중복 체크에 성공하였습니다."),
    SUCCESS_DUPLICATE_EMAIL("이메일 중복 체크에 성공하였습니다."),
    ;
    private final String message;

    SuccessOrFailMessage(String message) {
        this.message = message;
    }
}
