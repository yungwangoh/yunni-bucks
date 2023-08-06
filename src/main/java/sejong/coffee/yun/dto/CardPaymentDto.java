package sejong.coffee.yun.dto;

import lombok.Builder;
import sejong.coffee.yun.domain.pay.CardPayment;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@Builder
public class CardPaymentDto {
    public record Request (
        @NotNull(message = "카드번호가 없습니다.")
        String cardNumber,
        @Pattern(regexp="(?=.*[0-9]).{4}", message = "비밀번호는 숫자 4자여야 합니다.")
        String cardPassword,
        @NotNull(message = "만료 연도가 없습니다.")
        String cardExpirationYear,
        @NotNull(message = "만료 월이 없습니다.")
        String cardExpirationMonth,
        @NotNull(message = "주문 아이디가 없습니다.")
        String orderId,
        @NotNull(message = "주문명이 없습니다.")
        String orderName,
        @NotNull(message = "주문 금액이 없습니다.")
        String amount,
        @NotNull(message = "카드 소유자 번호가없습니다.")
        String customerIdentityNumber,
        String customerName
    ) {
        public static Request from(CardPayment cardPayment) {
            return new Request(cardPayment.getCardNumber(), cardPayment.getCardPassword(),
                    cardPayment.getCardExpirationYear(), cardPayment.getCardExpirationMonth(),
                    cardPayment.getOrder().mapOrderName(), cardPayment.getOrder().getName(),
                    cardPayment.getOrder().getOrderPrice().getTotalPrice().toString(),
                    cardPayment.getOrder().getMember().getEmail().split("@")[0],
                    cardPayment.getOrder().getMember().getName());
        }
    }

    public record Response (
        String orderId,
        String orderName,
        String method,
        String cardNumber,
        String totalAmount
    ) {
        public Response(CardPayment entity, String method) {
            this(entity.getOrder().mapOrderName(), entity.getOrder().getName(), method,
                    entity.getCardNumber(), entity.getOrder().getOrderPrice().getTotalPrice().toString());
        }
    }
}
