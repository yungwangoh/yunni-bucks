package sejong.coffee.yun.service.externalApi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import sejong.coffee.yun.dto.ocr.OcrDto;
import sejong.coffee.yun.infra.ApiService;
import sejong.coffee.yun.infra.ClovaApiServiceImpl;
import sejong.coffee.yun.infra.SystemUuidHolder;

import java.io.IOException;

import static sejong.coffee.yun.util.parse.ParsingUtil.parsingFileExtension;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class ClovaApiServiceTest extends InitializationApiData {

    @Value("${secrets.clova.authorizeUri}")
    private String authorizeUri;

    @Value("${secrets.clova.secret-key}")
    private String secretKey;

    @Test
    void 외부Api호출_메서드를_실행하여_응답값을_검증한다() throws IOException, InterruptedException {

        // given

        String username = "하윤";
        String path = "/Users/hayoon/Downloads/ocrtest/src/main/resources/static/images/img_1.png";
        OcrDto.Request request = OcrDto.Request.create(username, path, parsingFileExtension(path));

        ApiService apiService = new ApiService(null, new ClovaApiServiceImpl(authorizeUri, secretKey));

        // when
        OcrDto.Response response = apiService.callExternalOcrApi(request, new SystemUuidHolder());

        //then
        System.out.println(response);
    }
}
