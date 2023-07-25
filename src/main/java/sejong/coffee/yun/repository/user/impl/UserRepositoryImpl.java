package sejong.coffee.yun.repository.user.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.user.User;
import sejong.coffee.yun.repository.user.JpaUserRepository;
import sejong.coffee.yun.repository.user.UserRepository;

import java.util.List;

import static sejong.coffee.yun.domain.exception.ExceptionControl.NOT_FOUND_USER;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    @Override
    @Transactional
    public User save(User user) {
        return jpaUserRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        return getUser(id);
    }

    @Override
    public List<User> findAll() {
        return jpaUserRepository.findAll();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        jpaUserRepository.delete(getUser(id));
    }

    @Override
    public boolean exist(Long id) {
        return jpaUserRepository.existsById(id);
    }

    @Override
    @Transactional
    public void updateName(Long id, String name) {
        User user = getUser(id);

        user.updateName(name);
    }

    @Override
    @Transactional
    public void updatePassword(Long id, String password) {
        User user = getUser(id);

        user.updatePassword(password);
    }

    @Override
    @Transactional
    public void updateEmail(Long id, String email) {
        User user = getUser(id);

        user.updateEmail(email);
    }

    private User getUser(Long id) {
        return jpaUserRepository.findById(id)
                .orElseThrow(NOT_FOUND_USER::notFoundUserException);
    }
}
