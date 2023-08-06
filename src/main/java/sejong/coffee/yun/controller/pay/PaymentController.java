package sejong.coffee.yun.controller.pay;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sejong.coffee.yun.domain.pay.CardPayment;
import sejong.coffee.yun.dto.CardPaymentDto;
import sejong.coffee.yun.infra.TossAPIService;

import java.io.IOException;

@RestController
@RequestMapping("/pay")
@RequiredArgsConstructor
public class PaymentController {

    private final TossAPIService tossAPIService;

    @PostMapping("/v1/payments/card")
    public ResponseEntity<CardPayment> keyIn(@RequestBody CardPaymentDto.Request cardPaymentDto) throws IOException, InterruptedException {
        CardPayment cardPayment = tossAPIService.callExternalAPI(cardPaymentDto);
        return new ResponseEntity<>(cardPayment, HttpStatus.OK);
    }
}
