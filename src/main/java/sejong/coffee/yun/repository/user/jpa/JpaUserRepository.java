package sejong.coffee.yun.repository.user.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import sejong.coffee.yun.domain.user.Member;

public interface JpaUserRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
    boolean existsByName(String name);
}
