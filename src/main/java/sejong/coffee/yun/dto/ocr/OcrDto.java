package sejong.coffee.yun.dto.ocr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import sejong.coffee.yun.util.regex.RegexUtil;

import javax.validation.constraints.Pattern;

@Builder
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OcrDto {

    public record Request (
        String username,
        String path,
        @Pattern(regexp = RegexUtil.IMG_FORMAT)
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
