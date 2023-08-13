package sejong.coffee.yun.service.externalApi;

import org.junit.jupiter.api.Test;
import sejong.coffee.yun.controller.pay.CreatePaymentData;
import sejong.coffee.yun.domain.pay.PaymentStatus;
import sejong.coffee.yun.dto.pay.CardPaymentDto;
import sejong.coffee.yun.infra.ApiService;
import sejong.coffee.yun.infra.fake.FakeApiService;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static sejong.coffee.yun.dto.pay.CardPaymentDto.Request;

public class TossApiServiceTest extends CreatePaymentData {

    @Test
    public void 주문과_결제_정보가_제대로_만들어지는지_테스트() throws IOException, InterruptedException {
        //given
        FakeApiService fakeApiService = new FakeApiService("asdfasdf");
        ApiService apiService = new ApiService(fakeApiService);
        Request request = Request.from(cardPayment);

        //when
        CardPaymentDto.Response response = apiService.callApi(request);

        //then
        assertThat(response.orderName()).isEqualTo("커피 외 3개");
        assertThat(response.totalAmount()).isEqualTo("3000");
        assertThat(response.cardNumber()).containsAnyOf("12341234");
        assertThat(response.paymentStatus()).isEqualTo(PaymentStatus.DONE);
        assertThat(response.orderUuid()).isEqualTo("asdfasdf");
    }
}
