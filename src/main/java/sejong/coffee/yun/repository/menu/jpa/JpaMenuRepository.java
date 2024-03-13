package sejong.coffee.yun.repository.menu.jpa;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import sejong.coffee.yun.domain.order.menu.Menu;

import java.util.Optional;

public interface JpaMenuRepository extends JpaRepository<Menu, Long> {
    @NotNull
    //@Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Menu> findById(@NotNull Long menuId);
}
