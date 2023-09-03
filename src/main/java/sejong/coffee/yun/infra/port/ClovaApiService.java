package sejong.coffee.yun.infra.port;

import sejong.coffee.yun.dto.ocr.OcrDto;

import java.io.IOException;

public interface ClovaApiService {
    OcrDto.Response callExternalApi(OcrDto.Request request, UuidHolder uuidHolder) throws IOException, InterruptedException;
}
