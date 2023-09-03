package sejong.coffee.yun.service.externalApi;

import org.junit.jupiter.api.Test;
import sejong.coffee.yun.dto.ocr.OcrDto;
import sejong.coffee.yun.infra.ApiService;
import sejong.coffee.yun.infra.fake.FakeOcrApiService;
import sejong.coffee.yun.infra.fake.FakeUuidHolder;
import sejong.coffee.yun.util.parse.JsonParsing;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class FakeClovaApiServiceTest extends InitializationApiData {


    @Test
    public void jsonBody를_OcrDtoResponse에_Mapping하는지_테스트() throws IOException, InterruptedException {
        //given
        //when
        OcrDto.Response response = JsonParsing.parseOcrObjectByJson(this.jsonBody);

        //then
        assertThat(response.cardNumber()).isEqualTo("4910 2345 6789 0123");
        assertThat(response.validThru()).isEqualTo("07/22");

    }

    @Test
    void 외부Api호출로_Request와_Response가_올바르게_매핑되는지_검사한다() throws IOException, InterruptedException {
        //given
        ApiService apiService = new ApiService(null, new FakeOcrApiService());
        OcrDto.Request request = OcrDto.Request.create("하윤", "/download/home", "png");

        //when
        OcrDto.Response response = apiService.callExternalOcrApi(request, new FakeUuidHolder("uuid"));

        //then
        assertThat(response.requestId()).isEqualTo("uuid");
        assertThat(response.cardNumber()).isEqualTo("12341234");
        assertThat(response.validThru()).isEqualTo("12/23");
    }
}
