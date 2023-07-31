package sejong.coffee.yun.util.parse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import sejong.coffee.yun.dto.CardPaymentDto;

public class JsonWrite {

    public static String paymentJsonParsing(CardPaymentDto.Request requestBody) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(requestBody);
    }
}
