package sejong.coffee.yun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class YunApplication {

	public static void main(String[] args) {
		SpringApplication.run(YunApplication.class, args);
	}

}
