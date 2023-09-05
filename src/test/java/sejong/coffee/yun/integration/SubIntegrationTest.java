package sejong.coffee.yun.integration;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import sejong.coffee.yun.domain.user.Card;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.dto.card.CardDto;
import sejong.coffee.yun.dto.ocr.OcrDto;

import java.util.List;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class SubIntegrationTest extends MainIntegrationTest {

    public static final String CARD_API_PATH = "/api/card";
    public static final String OCR_API_PATH = "/api/ocr";
    public static final String PAY_API_PATH = "/api/pay";

    public CardDto.Request registerRequest() {
        return new CardDto.Request(card().getNumber(), card().getCardPassword(), card().getValidThru());
    }

    public CardDto.Request badCardNumberRegisterRequest() {
        return new CardDto.Request("9446032384143059944603238", card().getCardPassword(), card().getValidThru());
    }

    public CardDto.Request badCardPasswordRegisterRequest() {
        return new CardDto.Request(card().getNumber(), "12345", card().getValidThru());
    }

    public CardDto.Request badCardValidDateRegisterRequest() {
        return new CardDto.Request(card().getNumber(), card().getCardPassword(), "23/10");
    }

    public Card card() {
        return Card.builder()
                .member(member())
                .number("9446032384143059")
                .cardPassword("1234")
                .validThru("11/23")
                .build();
    }

    /**
     *
     * @param member // member().getName()
     * @param path //"/Users/hayoon/Downloads/ocrtest/src/main/resources/static/images/img_1.png"
     * @param format //.png
     * @return
     */

    public OcrDto.Request ocrRequest(Member member, String path, String format) {
        return OcrDto.Request.create(member.getName(), path, format);
    }

    public OcrDto.Request badOcrRequest(String username, String path, String format) {
        return new OcrDto.Request(username, path, format);
    }

    protected static List<FieldDescriptor> getCardRequests() {
        return List.of(
                fieldWithPath("number").type(JsonFieldType.STRING).description("카드 번호"),
                fieldWithPath("cardPassword").type(JsonFieldType.STRING).description("카드 비밀번호"),
                fieldWithPath("validThru").type(JsonFieldType.STRING).description("카드 유효연월")
        );
    }

    protected static List<FieldDescriptor> getCardResponses() {
        return List.of(
                fieldWithPath("cardId").type(JsonFieldType.NUMBER).description("카드 Id"),
                fieldWithPath("number").type(JsonFieldType.STRING).description("카드 번호"),
                fieldWithPath("cardPassword").type(JsonFieldType.STRING).description("카드 비밀번호"),
                fieldWithPath("validThru").type(JsonFieldType.STRING).description("카드 유효연월"),
                fieldWithPath("createAt").description("생성 시간")
        );
    }

    protected static List<FieldDescriptor> getOcrRequests() {
        return List.of(
                fieldWithPath("username").type(JsonFieldType.NUMBER).description("회원 이름"),
                fieldWithPath("path").type(JsonFieldType.STRING).description("이미지 경로"),
                fieldWithPath("format").type(JsonFieldType.STRING).description("이미지 확장자명")
        );
    }

    protected static List<FieldDescriptor> getOcrResponses() {
        return List.of(
                fieldWithPath("requestId").type(JsonFieldType.STRING).description("OCR 요청 ID"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("이미지 이름"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("OCR 상태"),
                fieldWithPath("cardNumber").type(JsonFieldType.STRING).description("카드 번호"),
                fieldWithPath("validThru").type(JsonFieldType.STRING).description("카드 유효연월")
        );
    }
}
