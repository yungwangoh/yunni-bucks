package sejong.coffee.yun.controller.ocr;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sejong.coffee.yun.custom.annotation.MemberId;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.dto.card.CardDto;
import sejong.coffee.yun.infra.ApiService;
import sejong.coffee.yun.infra.port.UuidHolder;
import sejong.coffee.yun.service.CardService;
import sejong.coffee.yun.service.UserService;

import java.io.IOException;

import static sejong.coffee.yun.dto.ocr.OcrDto.Request;
import static sejong.coffee.yun.dto.ocr.OcrDto.Response;
import static sejong.coffee.yun.util.parse.ParsingUtil.parsingFileExtension;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ocr")
public class OcrController {

    private final @Qualifier("clovaApiServiceImpl") ApiService apiService;
    private final UserService userService;
    private final CardService cardService;
    private final UuidHolder uuidHolder;

    @PostMapping("")
    public ResponseEntity<Response> cardOcr(@MemberId Long memberId, String cardPassword, String path) throws IOException, InterruptedException {
        Member findMember = userService.findMember(memberId);
        Request request = Request.create(findMember.getName(), path, parsingFileExtension(path));
        Response response = apiService.callExternalOcrApi(request, uuidHolder);
        cardService.create(memberId, CardDto.Request.create(response.cardNumber(), cardPassword, response.validThru()));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
