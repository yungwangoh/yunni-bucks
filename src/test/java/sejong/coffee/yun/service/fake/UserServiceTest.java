package sejong.coffee.yun.service.fake;

import sejong.coffee.yun.jwt.JwtProvider;
import sejong.coffee.yun.mock.repository.CustomValueOperationImpl;
import sejong.coffee.yun.mock.repository.FakeNoSqlRepository;
import sejong.coffee.yun.mock.repository.FakeOrderRepository;
import sejong.coffee.yun.mock.repository.FakeUserRepository;
import sejong.coffee.yun.service.UserService;

public class UserServiceTest {

    private final UserService userService;

    public UserServiceTest() {
        this.userService = new UserService(new FakeUserRepository(),
                new JwtProvider("key", 10000L, 100000L),
                new FakeNoSqlRepository(new CustomValueOperationImpl()), new FakeOrderRepository());
    }
}
