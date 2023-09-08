package sejong.coffee.yun.service.mock;

import sejong.coffee.yun.dto.ocr.OcrDto;
import sejong.coffee.yun.infra.port.ClovaApiService;
import sejong.coffee.yun.infra.port.UuidHolder;

public class FakeOcrApiService implements ClovaApiService {

    public OcrDto.Request ocrRequest;
    public OcrDto.Response ocrResponse;
    public String requestId;

    @Override
    public OcrDto.Response callExternalApi(OcrDto.Request request, UuidHolder uuidHolder) {
        this.requestId = uuidHolder.random();
        this.ocrRequest = request;
        this.ocrResponse = OcrDto.Response.create(requestId, request.username(), "SUCCESS", "12341234", "12/23");
        return this.ocrResponse;
    }
}
