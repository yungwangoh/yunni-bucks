package sejong.coffee.yun.repository.menu.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import sejong.coffee.yun.domain.order.menu.Menu;

public interface JpaMenuRepository extends JpaRepository<Menu, Long> {
}
