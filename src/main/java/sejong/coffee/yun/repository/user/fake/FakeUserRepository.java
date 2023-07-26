package sejong.coffee.yun.repository.user.fake;

import sejong.coffee.yun.domain.exception.ExceptionControl;
import sejong.coffee.yun.domain.user.User;
import sejong.coffee.yun.repository.user.UserRepository;

import java.util.*;

import static sejong.coffee.yun.domain.exception.ExceptionControl.*;

public class FakeUserRepository implements UserRepository {

    private final List<User> users = new ArrayList<>();
    private Long id = 0L;

    @Override
    public User save(User user) {
        User newUser = User.from(++id, user);
        users.add(newUser);

        return newUser;
    }

    @Override
    public User findById(Long id) {
        for(User user : users) {
            if(Objects.equals(user.getId(), id)) {
                return user;
            }
        }

        throw NOT_FOUND_USER.notFoundUserException();
    }

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public boolean exist(Long id) {
        try {
            findById(id);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public void delete(Long id) {
        User user = findById(id);
        users.remove(user);
    }

    @Override
    public void updateName(Long id, String name) {
        User user = findById(id);
        user.updateName(name);
    }

    @Override
    public void updatePassword(Long id, String password) {
        User user = findById(id);
        user.updatePassword(password);
    }

    @Override
    public void updateEmail(Long id, String email) {
        User user = findById(id);
        user.updateEmail(email);
    }
}
