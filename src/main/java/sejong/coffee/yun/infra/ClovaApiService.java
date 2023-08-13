package sejong.coffee.yun.infra;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ClovaApiService {

    private final String authorizeUri;
    private final String secretKey;

    public ClovaApiService(@Value("${secrets.clova.authorizeUri}") final String authorizeUri,
                           @Value("${secrets.clova.secret-key}") final String secretKey) {

        this.authorizeUri = authorizeUri;
        this.secretKey = secretKey;
    }
}
