package sejong.coffee.yun.controller.ocr;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import sejong.coffee.yun.domain.user.Card;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.infra.port.UuidHolder;
import sejong.coffee.yun.integration.MainIntegrationTest;
import sejong.coffee.yun.mapper.CustomMapper;
import sejong.coffee.yun.repository.card.CardRepository;
import sejong.coffee.yun.repository.user.UserRepository;
import sejong.coffee.yun.service.CardService;
import sejong.coffee.yun.service.UserService;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OcrControllerTest extends MainIntegrationTest {

    @Autowired
    private CustomMapper customMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CardService cardService;

    @Autowired
    private UuidHolder uuidHolder;

    @Nested
    @DisplayName("OCR 통합 테스트")
    @Sql(value = {"/sql/user.sql", "/sql/menu.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
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
        @DisplayName("유저가 OCR을 통해 카드를 등록한다")
        public void testCardOcr() throws Exception {

            String cardPassword = "1234";
            String path = "/Users/hayoon/Downloads/ocrtest/src/main/resources/static/images/img_1.png";

            // MockMvc를 사용하여 요청.
            mockMvc.perform(MockMvcRequestBuilders.post("/api/ocr")
                            .header(HttpHeaders.AUTHORIZATION, token)
                            .param("cardPassword", cardPassword)
                            .param("path", path)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("SUCCESS"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.cardNumber").value("9446032384143059"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.validThru").value("09/23"));

            // 이후에 cardService.create()에 대한 행위 검증 및 기타 테스트 케이스 추가
            // 이전 테스트에서 생성된 카드 정보를 데이터베이스에서 조회
            Member findMember = userRepository.findByEmail("qwer1234@naver.com");
            Card card = cardService.getByMemberId(findMember.getId());

            // 조회된 카드 정보와 예상 값 비교
            Assertions.assertNotNull(card);
            Assertions.assertEquals("9446032384143059", card.getNumber());
            Assertions.assertEquals("09/23", card.getValidThru());
        }
    }
}
