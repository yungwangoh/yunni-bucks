package sejong.coffee.yun.dto.ocr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OcrDto {

    public record Request (
        String username,
        String path,
        String format
    ) {
        public static Request create(String username, String path, String format) {
            return new Request(username, path, format);
        }
    }

    public record Response (
            String requestId,
            String name,
            String message,
            String cardNumber,
            String validThru
    ) {
        public static Response create(String requestId,  String name,
                                      String message, String cardNumber, String validThru) {
            return new Response(requestId, name, message, cardNumber, validThru);
        }
    }
}
