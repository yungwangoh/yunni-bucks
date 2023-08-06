package sejong.coffee.yun.util.parse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import sejong.coffee.yun.domain.pay.CardPayment;
import sejong.coffee.yun.dto.CardPaymentDto;

@RequiredArgsConstructor
public class JsonParsing {
    protected static ObjectMapper objectMapper;

    public static String parsePaymentStringByJson(CardPaymentDto.Request request) throws JsonProcessingException {
        return objectMapper.writeValueAsString(request);
    }

    public static String parsePaymentStringByJson(CardPaymentDto.Response response) throws JsonProcessingException {
        return objectMapper.writeValueAsString(response);
    }

    public static CardPayment parsePaymentObjectByJson(String responseBody) throws JsonProcessingException {
        return objectMapper.readValue(responseBody, CardPayment.class);
    }
}
