package sejong.coffee.yun.infra.fake;

import sejong.coffee.yun.domain.pay.PaymentStatus;
import sejong.coffee.yun.infra.port.TossApiService;

import java.io.IOException;

import static sejong.coffee.yun.dto.pay.CardPaymentDto.Request;
import static sejong.coffee.yun.dto.pay.CardPaymentDto.Response;

public class FakeApiService implements TossApiService {

    public Request request;
    public Response response;
    public String paymentKey;

    public FakeApiService(String paymentKey) {
        this.paymentKey = paymentKey;
    }

    @Override
    public Response callExternalAPI(Request cardPaymentDto) throws IOException, InterruptedException {
        this.request = cardPaymentDto;
        this.response = new Response(request.orderUuid(), request.orderName(), request.cardNumber(), request.amount(), this.paymentKey,
                PaymentStatus.DONE, request.requestedAt(), request.requestedAt().plusSeconds(5));
        return this.response;
    }
}
