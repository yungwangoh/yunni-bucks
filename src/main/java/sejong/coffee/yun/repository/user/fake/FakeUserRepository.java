package sejong.coffee.yun.repository.user.fake;

import sejong.coffee.yun.domain.user.User;
import sejong.coffee.yun.repository.user.UserRepository;

import java.util.*;

public class FakeUserRepository implements UserRepository {

    private final List<sejong.coffee.yun.fake.User> list = new ArrayList<>();
    private Long id;

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public User findById(Long id) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public boolean exist(Long id) {
        return false;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void updateName(Long id, String name) {

    }

    @Override
    public void updatePassword(Long id, String password) {

    }

    @Override
    public void updateEmail(Long id, String email) {

    }
}
