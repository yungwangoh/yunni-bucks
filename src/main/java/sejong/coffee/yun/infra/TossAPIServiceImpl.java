package sejong.coffee.yun.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sejong.coffee.yun.dto.CardPaymentDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static sejong.coffee.yun.util.parse.JsonParsing.parsePaymentObjectByJson;
import static sejong.coffee.yun.util.parse.JsonParsing.parsePaymentStringByJson;

@Service
public class TossAPIServiceImpl implements TossAPIService{

    private final String apiUri;
    private final String secretKey;

    public TossAPIServiceImpl(@Value("${secrets.toss.apiUri}") final String apiUri,
                              @Value("${secrets.toss.secret-key}") final String secretKey) {

        this.apiUri = apiUri;
        this.secretKey = secretKey;
    }

    @Override
    public CardPaymentDto.Response callExternalAPI(CardPaymentDto.Request cardPaymentDto) throws IOException, InterruptedException {
        // 외부 API 호출하는 로직
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUri))
                .header("Authorization", secretKey)
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(parsePaymentStringByJson(cardPaymentDto)))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        // 예외처리(토스 커스텀 -> 내부 프로젝트 커스텀) TODO
        return parsePaymentObjectByJson(response.body());
    }
}
