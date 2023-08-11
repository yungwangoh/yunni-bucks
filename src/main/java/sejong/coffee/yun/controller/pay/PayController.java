package sejong.coffee.yun.controller.pay;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sejong.coffee.yun.dto.CardPaymentDto;
import sejong.coffee.yun.infra.TossAPIService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static sejong.coffee.yun.util.parse.JsonWrite.paymentJsonParsing;

@RestController
@RequestMapping("/pay")
@RequiredArgsConstructor
public class PayController {

    /**
     * --data '{
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

    private final TossAPIService tossAPIService;

    @PostMapping("/v1/payments/card")
    public ResponseEntity<String> keyIn(@RequestBody CardPaymentDto.Request cardPaymentDto) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tossAPIService.getApiUri()))
                .header("Authorization", tossAPIService.getSecretKey())
                .header("Content-Type", "application/json")
//                .method("POST", HttpRequest.BodyPublishers.ofString("{\"amount\":15000,\"orderId\":\"afm4N87tU_uJNHayoon_1ROjD_\",\"orderName\":\"아메리카노 외 2건\",\"customerName\":\"하윤\",\"cardNumber\":\""+ cardPaymentDto.getCardNumber() +"\",\"cardExpirationYear\":\""+ validYear+"\",\"cardExpirationMonth\":\""+ validMonth+"\",\"cardPassword\":\"12\",\"customerIdentityNumber\":\"881212\"}"))
                .method("POST", HttpRequest.BodyPublishers.ofString(paymentJsonParsing(cardPaymentDto)))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        return new ResponseEntity<>(response.body(), HttpStatus.OK);
    }
}
