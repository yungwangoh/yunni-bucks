package sejong.coffee.yun.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClovaAPIService {

    private final String authorizeUri;
    private final String secretKey;

    public ClovaAPIService(@Value("${secrets.clova.authorizeUri}") final String authorizeUri,
                          @Value("${secrets.clova.secret-key}") final String secretKey) {

        this.authorizeUri = authorizeUri;
        this.secretKey = secretKey;
    }
}
