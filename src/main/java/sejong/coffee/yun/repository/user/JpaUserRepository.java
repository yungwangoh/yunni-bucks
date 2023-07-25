package sejong.coffee.yun.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import sejong.coffee.yun.domain.user.User;

public interface JpaUserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Query("update User u set u.name = :name where u.id = :id")
    void updateName(Long id, String name);
}
