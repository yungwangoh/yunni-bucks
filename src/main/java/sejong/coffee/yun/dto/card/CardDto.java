package sejong.coffee.yun.dto.card;

import sejong.coffee.yun.domain.user.Card;
import sejong.coffee.yun.util.regex.RegexUtil;

import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

public class CardDto {

    public record Request (
        @Pattern(regexp = RegexUtil.CARD_NUMBER, message = "카드번호는 20자 이하여야 합니다.")
        String number,
        @Pattern(regexp = RegexUtil.CARD_PASSWORD, message = "비밀번호는 숫자 4자여야 합니다.")
        String cardPassword,
        @Pattern(regexp = RegexUtil.CARD_VALID_THRU, message = "유효기간 포맷이 맞지 않습니다.")
        String validThru
    ) {
        public static CardDto.Request create(String number, String cardPassword, String validThru) {
            return new CardDto.Request(number, cardPassword, validThru);
        }
    }

    public record Response (
            Long cardId,
            String number,
            String cardPassword,
            String validThru,
            LocalDateTime createAt
    ) {
        public Response(Card card) {
            this(card.getId(), card.getNumber(), card.getCardPassword(), card.getValidThru(), card.getCreateAt());
        }
    }
}
