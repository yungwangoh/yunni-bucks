package sejong.coffee.yun.domain.pay;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


class CardPaymentTest extends BeforeCreatedData {

    /**--data '{
     * "amount":15000,
     * "orderId":"a4CWyWY5m89PNh7xJwhk1",
     * "orderName":"토스 티셔츠 외 2건",
     * "customerName":"박토스",
     * "cardNumber":"4330123412341234",
     * "cardExpirationYear":"24",
     * "cardExpirationMonth":"07",
     * "cardPassword":"12",
     * "customerIdentityNumber":"881212"
     * }'
     */

    @Test
    void 카드결제_단건_유효기간_검증() {
        //given

        //when
        CardPayment cardPayment = new CardPayment(order, card);

        //then
        assertThat(cardPayment.getCardExpirationYear())
                .isGreaterThanOrEqualTo(String.valueOf(LocalDateTime.now().getDayOfYear()).substring(0, 2))
                .isLessThanOrEqualTo("99");

        assertThat(cardPayment.getCardExpirationMonth())
                .isGreaterThanOrEqualTo("01")
                .isLessThanOrEqualTo("12");
    }
}