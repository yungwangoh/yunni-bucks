package sejong.coffee.yun.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import sejong.coffee.yun.domain.user.User;

public interface JpaUserRepository extends JpaRepository<User, Long> {
}
