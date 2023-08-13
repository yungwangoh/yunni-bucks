package sejong.coffee.yun.util.parse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import sejong.coffee.yun.dto.pay.CardPaymentDto;


public class JsonParsing {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String parsePaymentStringByJson(CardPaymentDto.Request request) throws JsonProcessingException {
        return objectMapper.writeValueAsString(request);
    }

    public static String parsePaymentStringByJson(CardPaymentDto.Response response) throws JsonProcessingException {
        return objectMapper.writeValueAsString(response);
    }

    public static CardPaymentDto.Response parsePaymentObjectByJson(String responseBody) throws JsonProcessingException {
        return objectMapper.readValue(responseBody, CardPaymentDto.Response.class);
    }
}
