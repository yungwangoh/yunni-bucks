package sejong.coffee.yun.service.externalApi;

import org.junit.jupiter.api.Test;
import sejong.coffee.yun.controller.pay.CreatePaymentData;
import sejong.coffee.yun.domain.pay.PaymentStatus;
import sejong.coffee.yun.dto.pay.CardPaymentDto;
import sejong.coffee.yun.infra.ApiService;
import sejong.coffee.yun.service.mock.FakeTossApiService;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static sejong.coffee.yun.dto.pay.CardPaymentDto.Request;

public class TossApiServiceTest extends CreatePaymentData {

    @Test
    public void 주문과_결제_정보가_제대로_만들어지는지_테스트() throws IOException, InterruptedException {
        //given
        FakeTossApiService fakeTossApiService = new FakeTossApiService("asdfasdf");
        ApiService apiService = new ApiService(fakeTossApiService, null);
        Request request = Request.from(cardPayment);

        //when
        CardPaymentDto.Response response = apiService.callExternalPayApi(request);

        //then
        assertThat(response.orderName()).isEqualTo("커피 외 3개");
        assertThat(response.totalAmount()).isEqualTo("3000");
        assertThat(isMaskingMatch(request.cardNumber(), response.cardNumber())).isTrue();
        assertThat(response.paymentStatus()).isEqualTo(PaymentStatus.DONE);
        assertThat(response.orderUuid()).isEqualTo("asdfasdf");
    }

    public static boolean isMaskingMatch(String c, String masking) {
        if (c.length() != masking.length()) {
            return false; // 길이가 다르면 일치하지 않음
        }

        for (int i = 0; i < c.length(); i++) {
            if (masking.charAt(i) != '*' && masking.charAt(i) != c.charAt(i)) {
                return false; // masking된 문자열과 다른 문자 발견
            }
        }

        return true; // 모든 문자가 일치
    }
}
