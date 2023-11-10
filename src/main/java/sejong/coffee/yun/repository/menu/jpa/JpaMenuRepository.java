package sejong.coffee.yun.repository.menu.jpa;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import sejong.coffee.yun.domain.order.menu.Menu;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface JpaMenuRepository extends JpaRepository<Menu, Long> {
    @NotNull
    @Lock(LockModeType.OPTIMISTIC)
    Optional<Menu> findById(@NotNull Long menuId);
}
