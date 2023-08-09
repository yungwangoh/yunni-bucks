package sejong.coffee.yun.infra;

import sejong.coffee.yun.dto.CardPaymentDto;

import java.io.IOException;

public interface TossAPIService {
    CardPaymentDto.Response callExternalAPI(CardPaymentDto.Request cardPaymentDto) throws IOException, InterruptedException;
}
