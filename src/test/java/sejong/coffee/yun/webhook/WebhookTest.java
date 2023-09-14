package sejong.coffee.yun.webhook;

import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import sejong.coffee.yun.integration.SubIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

public class WebhookTest extends SubIntegrationTest {

    @Test
    void test() {
        String token = "T05S4KA8C9F/B05S4L85NAV/jW3SWBKcl8kThXBY3tvlRyYz";
        String webhookUrl = "https://hooks.slack.com/services/" + token;
        SlackApi api = new SlackApi(webhookUrl);
        api.call(new SlackMessage("Hello SpringBoot Test!"));
    }

    @Test
    void errorTest() throws Exception {
        mockMvc.perform(get(CARD_API_PATH + "/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        throw new Exception();
    }
}
