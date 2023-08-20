package sejong.coffee.yun.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sejong.coffee.yun.infra.port.TossApiService;

import java.io.IOException;

import static sejong.coffee.yun.dto.pay.CardPaymentDto.Request;
import static sejong.coffee.yun.dto.pay.CardPaymentDto.Response;

@Service
@RequiredArgsConstructor
public class ApiService {

    private final TossApiService tossApiService;

    public Response callApi(Request request) throws IOException, InterruptedException {
        return tossApiService.callExternalAPI(request);
    }
}
