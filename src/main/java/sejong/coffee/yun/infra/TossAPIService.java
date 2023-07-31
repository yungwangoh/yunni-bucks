package sejong.coffee.yun.infra;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class TossAPIService {

    private final String apiUri;
    private final String secretKey;

    public TossAPIService(@Value("${secrets.toss.apiUri}") final String apiUri,
                          @Value("${secrets.toss.secret-key}") final String secretKey) {

        this.apiUri = apiUri;
        this.secretKey = secretKey;
    }
}
