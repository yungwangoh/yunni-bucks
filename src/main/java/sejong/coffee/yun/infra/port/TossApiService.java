package sejong.coffee.yun.infra.port;

import sejong.coffee.yun.dto.CardPaymentDto;

import java.io.IOException;

public interface TossApiService {
    CardPaymentDto.Response callExternalAPI(CardPaymentDto.Request cardPaymentDto) throws IOException, InterruptedException;
}
