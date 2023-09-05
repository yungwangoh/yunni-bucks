package sejong.coffee.yun.integration.ocr;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import sejong.coffee.yun.integration.SubIntegrationTest;
import sejong.coffee.yun.repository.card.CardRepository;
import sejong.coffee.yun.repository.user.UserRepository;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OcrIntegrationTest extends SubIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    @Nested
    @DisplayName("OCR 통합 테스트")
    @Sql(value = {"/sql/user.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/truncate.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    class OcrTest {
        String token;

        @BeforeEach
        void init() throws Exception {
            token = signInModule();
        }

        @AfterEach
        void initDB() {
            cardRepository.clear();
            userRepository.clear();
        }

        @Test
        @DisplayName("유저가 OCR을 통해 카드를 등록 Http Status Code 201")
        public void ocr() throws Exception {

            String cardPassword = "1234";
            String path = "/Users/hayoon/Downloads/ocrtest/src/main/resources/static/images/img_1.png";

            ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(OCR_API_PATH)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .param("cardPassword", cardPassword)
                    .param("path", path)
                    .contentType(MediaType.APPLICATION_JSON));

            resultActions
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.cardNumber").value("9446032384143059"))
                    .andExpect(jsonPath("$.validThru").value("09/23"))
                    .andDo(document("ocr-create",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            ),
                            requestParameters(
                                    parameterWithName("cardPassword").description("카드 비밀번호"),
                                    parameterWithName("path").description("경로")
                            ),
                            responseFields(
                                    getOcrResponses()
                            )
                    ));

        }

        @Test
        @DisplayName("확장자 포맷 검증실패 Http Status Code 500")
        public void ocrFailed() throws Exception {

            String cardPassword = "1234";
            String path = "/Users/hayoon/Downloads/ocrtest/src/main/resources/static/images/img_1.pngg";

            ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(OCR_API_PATH)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .param("cardPassword", cardPassword)
                    .param("path", path)
                    .contentType(MediaType.APPLICATION_JSON));

            resultActions
                    .andExpect(status().isInternalServerError())
                    .andDo(document("invalid-ocr-file-extension",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("엑세스 토큰")
                            ),
                            requestParameters(
                                    parameterWithName("cardPassword").description("카드 비밀번호"),
                                    parameterWithName("path").description("경로")
                            ),
                            responseFields(
                                    getFailResponses()
                            )
                    ));
        }
    }
}
