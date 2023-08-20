package sejong.coffee.yun.dto.pay;

import org.springframework.data.domain.PageImpl;
import sejong.coffee.yun.domain.pay.CardPayment;

import java.util.List;

public class CardPaymentPageDto {
    public record Response(int pageNumber, List<CardPaymentDto.Response> cardPayments) {

        public Response(PageImpl<CardPayment> page) {
            this(page.getNumber(), page.getContent()
                    .stream()
                    .map(CardPaymentDto.Response::new)
                    .toList()
            );
        }
    }
}
