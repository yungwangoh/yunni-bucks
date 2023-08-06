package sejong.coffee.yun.repository.menulist.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import sejong.coffee.yun.domain.order.MenuList;

public interface JpaMenuListRepository extends JpaRepository<MenuList, Long> {
}
