package sejong.coffee.yun.fake;

public class User {
    private final Long id;
    private final sejong.coffee.yun.domain.user.User user;

    public User(Long id, sejong.coffee.yun.domain.user.User user) {
        assert id > 0;
        assert user != null;
        this.id = id;
        this.user = user;
    }
}
