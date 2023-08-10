package sejong.coffee.yun.controller.pay;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sejong.coffee.yun.domain.pay.CardPayment;
import sejong.coffee.yun.mapper.CustomMapper;
import sejong.coffee.yun.service.PayService;

import java.io.IOException;

import static sejong.coffee.yun.dto.CardPaymentDto.Request;
import static sejong.coffee.yun.dto.CardPaymentDto.Response;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PayService payService;
    private final CustomMapper customMapper;

    @PostMapping("/card-payment/{orderId}")
    public ResponseEntity<Response> keyIn(
            @PathVariable Long orderId) throws IOException, InterruptedException {
        Request request = payService.initPayment(orderId);
        CardPayment cardPayment = payService.pay(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customMapper.map(cardPayment, Response.class));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Response> getByOrderId(@PathVariable String orderId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(customMapper.map(payService.getByOrderId(orderId), Response.class));
    }

    @GetMapping("/{paymentKey}")
    public ResponseEntity<Response> getByPaymentKey(@PathVariable String paymentKey) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(customMapper.map(payService.getByPaymentKey(paymentKey), Response.class));
    }
}
