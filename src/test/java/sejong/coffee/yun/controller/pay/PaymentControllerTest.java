package sejong.coffee.yun.controller.pay;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import sejong.coffee.yun.dto.CardPaymentDto;
import sejong.coffee.yun.infra.ApiService;
import sejong.coffee.yun.util.parse.JsonParsing;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(PaymentController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest extends CreatePaymentData {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ApiService apiService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testKeyIn() throws Exception {

        CardPaymentDto.Response mockResponse = new CardPaymentDto.Response(cardPayment);

        // Serialize the mock response to JSON
        String mockResponseJson = objectMapper.writeValueAsString(mockResponse);
        CardPaymentDto.Response parsingCardPayment = JsonParsing.parsePaymentObjectByJson(mockResponseJson);
        given(apiService.callApi(any(CardPaymentDto.Request.class))).willReturn(parsingCardPayment);

        CardPaymentDto.Request request = CardPaymentDto.Request.from(cardPayment);

        mockMvc.perform(post("/card-payment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.cardNumber").value("12341234****123*")) //마스킹 범위를 아직 체크 못 함
                .andExpect(jsonPath("$.orderName").value("커피 외 3개"))
                .andExpect(jsonPath("$.orderId", endsWith("00000")))
                .andExpect(jsonPath("$.totalAmount").value("3000.0"))
                .andExpect(jsonPath("$.method").value("카드"))
                .andExpect(jsonPath("$.cardNumber", containsString("12341234")));

    }
}