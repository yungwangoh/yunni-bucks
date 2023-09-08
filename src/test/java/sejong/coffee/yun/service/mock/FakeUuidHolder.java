package sejong.coffee.yun.service.mock;

import lombok.RequiredArgsConstructor;
import sejong.coffee.yun.infra.port.UuidHolder;

@RequiredArgsConstructor
public class FakeUuidHolder implements UuidHolder {

    private final String uuid;

    @Override
    public String random() {
        return uuid;
    }
}
