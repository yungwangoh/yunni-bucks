package sejong.coffee.yun.webhook;

import org.springframework.beans.factory.annotation.Value;

public class DiscordWebHookService implements WebHookService {

    @Value("${webhook.discord}")
    private String webHookUrl;

    @Override
    public void event(String msg) {

    }
}
