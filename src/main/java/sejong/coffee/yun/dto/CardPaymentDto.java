package sejong.coffee.yun.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class CardPaymentDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotNull(message = "카드번호가 없습니다.")
        private String cardNumber;
        @Pattern(regexp="(?=.*[0-9]).{4}", message = "비밀번호는 숫자 4자여야 합니다.")
        private String cardPassword;
        @NotNull(message = "만료 연도가 없습니다.")
        private String cardExpirationYear;
        @NotNull(message = "만료 월이 없습니다.")
        private String cardExpirationMonth;
        @NotNull(message = "주문 아이디가 없습니다.")
        private String orderId;
        @NotNull(message = "주문명이 없습니다.")
        private String orderName;
        @NotNull(message = "주문 금액이 없습니다.")
        private String amount;
        @NotNull(message = "카드 소유자 번호가없습니다.")
        private String customerIdentityNumber;
        private String customerName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String cardNumber;
        private String cardExpirationYear;
        private String cardExpirationMonth;
        private String cardPassword;
        private String amount;
        private String orderName;
    }
}
