package sejong.coffee.yun.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import sejong.coffee.yun.infra.ClovaApiServiceImpl;
import sejong.coffee.yun.infra.port.ClovaApiService;

@TestConfiguration
@TestPropertySource(locations = "classpath:application.yml")
public class TestConfig {

    @Bean
    public ClovaApiService clovaApiService(
            @Value("${secrets.clova.authorizeUri}") String authorizeUri,
            @Value("${secrets.clova.secret-key}") String secretKey) {
        return new ClovaApiServiceImpl(authorizeUri, secretKey);
    }
}
