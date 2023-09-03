package sejong.coffee.yun.dto.card;

import sejong.coffee.yun.domain.user.Card;

import javax.validation.constraints.Pattern;

public class CardDto {

    public record Request (
        @Pattern(regexp = "(?=.*[0-9]).{1,20}", message = "카드번호는 20자 이하여야 합니다.")
        String number,
        @Pattern(regexp = "(?=.*[0-9]).{4}", message = "비밀번호는 숫자 4자여야 합니다.")
        String cardPassword,
        @Pattern(regexp = "\"\\\\d{2}/\\\\d{2}\"", message = "유효기간 포맷이 맞지 않습니다.")
        String validThru
    ) {
        public static CardDto.Request create(String number, String cardPassword, String validThru) {
            return new CardDto.Request(number, cardPassword, validThru);
        }
    }

    public record Response (
            String number,
            String cardPassword,
            String validThru
    ) {
        public Response(Card card) {
            this(card.getNumber(), card.getCardPassword(), card.getValidThru());
        }
    }
}
