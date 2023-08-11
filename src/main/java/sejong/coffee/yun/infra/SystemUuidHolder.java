package sejong.coffee.yun.infra;

import org.springframework.stereotype.Component;
import sejong.coffee.yun.infra.port.UuidHolder;

import java.util.UUID;

@Component
public class SystemUuidHolder implements UuidHolder {

    @Override
    public String random() {
        return UUID.randomUUID().toString();
    }
}
