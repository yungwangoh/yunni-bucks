package sejong.coffee.yun.repository.user;

import sejong.coffee.yun.domain.user.User;

import java.util.List;

public interface UserRepository {

    User save(User user);
    User findById(Long id);
    List<User> findAll();
    boolean exist(Long id);
    void delete(Long id);
    void updateName(Long id, String name);
    void updatePassword(Long id, String password);
    void updateEmail(Long id, String email);
}
