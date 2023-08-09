package sejong.coffee.yun.dto;

import lombok.Builder;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.pay.CardPayment;
import sejong.coffee.yun.domain.pay.PaymentStatus;
import sejong.coffee.yun.domain.user.Card;
import sejong.coffee.yun.infra.port.UuidHolder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

import static sejong.coffee.yun.util.parse.ParsingDateTimeUtil.parsingCardValidDate;


@Builder
public class CardPaymentDto {
    public record Request(
            @NotNull(message = "카드번호가 없습니다.")
            String cardNumber,
            @Pattern(regexp = "(?=.*[0-9]).{4}", message = "비밀번호는 숫자 4자여야 합니다.")
            String cardPassword,
            @NotNull(message = "만료 연도가 없습니다.")
            String cardExpirationYear,
            @NotNull(message = "만료 월이 없습니다.")
            String cardExpirationMonth,
            @NotNull(message = "주문 아이디가 없습니다.")
            String orderUuid,
            @NotNull(message = "주문명이 없습니다.")
            String orderName,
            @NotNull(message = "주문 금액이 없습니다.")
            String amount,
            @NotNull(message = "카드 소유자 번호가없습니다.")
            String customerIdentityNumber,
            String customerName,
            LocalDateTime requestedAt
    ) {
        public static Request create(Card card, Order order, UuidHolder uuidHolder) {
            return new CardPaymentDto.Request(card.getNumber(), card.getCardPassword(), parsingCardValidDate(card.getValidThru())[0],
                    parsingCardValidDate(card.getValidThru())[1], uuidHolder.random(), order.getName(), order.getOrderPrice().getTotalPrice().toString(),
                    card.getMember().getEmail(), card.getMember().getName(), LocalDateTime.now());
        }

        public static Request from(CardPayment cardPayment) {
            return new Request(cardPayment.getCardNumber(), cardPayment.getCardPassword(),
                    cardPayment.getCardExpirationYear(), cardPayment.getCardExpirationMonth(),
                    cardPayment.getOrder().mapOrderName(), cardPayment.getOrder().getName(),
                    cardPayment.getOrder().getOrderPrice().getTotalPrice().toString(),
                    cardPayment.getOrder().getMember().getEmail().split("@")[0],
                    cardPayment.getOrder().getMember().getName(), cardPayment.getRequestedAt());
        }
    }

    public record Response(
            String orderUuid,
            String orderName,
            String cardNumber,
            String totalAmount,
            String paymentKey,
            PaymentStatus paymentStatus,
            LocalDateTime requestedAt,
            LocalDateTime approvedAt
    ) {
        public Response(CardPayment entity) {
            this(entity.getOrder().mapOrderName(), entity.getOrder().getName(),
                    entity.getCardNumber(), entity.getOrder().getOrderPrice().getTotalPrice().toString(),
                    entity.getPaymentKey(), entity.getPaymentStatus(),
                    entity.getRequestedAt(), entity.getApprovedAt());
        }
    }
}
